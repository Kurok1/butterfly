package indi.butterfly.executor;

import indi.butterfly.Message;
import indi.butterfly.MessageFactory;
import indi.butterfly.ResponseMessage;
import indi.butterfly.core.ButterflyMessage;
import indi.butterfly.core.ButterflyMessageSender;
import indi.butterfly.domain.DatasourceConfig;
import indi.butterfly.domain.Node;
import indi.butterfly.repository.DatasourceConfigRepository;
import indi.butterfly.repository.NodeRepository;
import indi.butterfly.template.DatabaseReadTemplate;
import indi.butterfly.util.ExecutorFactory;
import indi.butterfly.util.TextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Driver;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据库读取
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.07
 */
@Service
public class DatabaseReadExecutor implements IExecutor {

    private final Logger logger = LoggerFactory.getLogger(DatabaseReadExecutor.class);

    private final JdbcTemplate jdbcTemplate;

    private final DatasourceConfigRepository datasourceConfigRepository;

    private final ButterflyMessageSender sender;

    private final NodeRepository nodeRepository;

    private NamedParameterJdbcTemplate parameterJdbcTemplate = null;//named参数jdbcTemplate

    private DatabaseReadTemplate readTemplate = null;

    @Autowired
    public DatabaseReadExecutor(JdbcTemplate jdbcTemplate, DatasourceConfigRepository datasourceConfigRepository, ButterflyMessageSender messageSender, NodeRepository nodeRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.datasourceConfigRepository = datasourceConfigRepository;
        this.sender = messageSender;
        this.nodeRepository = nodeRepository;
    }

    @Override
    public Message beforeExecute(ButterflyMessage message) {
        //读取配置
        readTemplate = TextUtil.readJson(message.getConfigJson(), DatabaseReadTemplate.class);
        if (readTemplate == null) {
            logger.error("配置数据错误: {}", message.getConfigJson());
            return MessageFactory.error("读取配置数据错误");
        }
        //查找数据源配置
        DatasourceConfig datasourceConfig = this.datasourceConfigRepository.findFirstByCode(readTemplate.getDatasource()).orElse(null);
        if (datasourceConfig == null) {
            logger.error("未找到相应的数据源: {}", readTemplate.getDatasource());
            return MessageFactory.error(String.format("未找到相应的数据源 %s", readTemplate.getDatasource()));
        }
        //初始化数据源给jdbcTemplate
        Driver driver = null;
        try {
            driver = (Driver) Class.forName(datasourceConfig.getDriverClass()).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            logger.error("暂不支持的数据库驱动: {}", datasourceConfig.getDriverClass());
            return MessageFactory.error(String.format("暂不支持的数据库驱动 %s", datasourceConfig.getDriverClass()));
        }
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource(driver, datasourceConfig.getUrl(), datasourceConfig.getUser(), datasourceConfig.getPassword());
        this.jdbcTemplate.setDataSource(dataSource);

        if (StringUtils.isEmpty(readTemplate.getSql()))
            return MessageFactory.error("sql为空!");

        return MessageFactory.success();
    }

    @Override
    @KafkaListener(topics = {"butterfly-database-read"}, id = "butterfly.database.read")
    public ResponseMessage<Object> execute(ButterflyMessage message) {
        //执行逻辑
        //预处理
        Message result = beforeExecute(message);
        if (result.isError()) {
            //记录日志
            logger.error("预处理发生错误: {}", result.getMsg());
            //记录处理日志
            return MessageFactory.errorResponse(result.getMsg());
        }

        List<Map<String, Object>> queryResult = new LinkedList<>();
        if (readTemplate.getParamType() == 1) {
            parameterJdbcTemplate = new NamedParameterJdbcTemplate(this.jdbcTemplate);
            queryResult = parameterJdbcTemplate.queryForList(readTemplate.getSql(), message.getRequestParam());
        } else {
            Object[] objects = TextUtil.asArray(message.getRequestBody());
            queryResult = this.jdbcTemplate.queryForList(readTemplate.getSql(), objects);
        }
        if (queryResult.size() > 0 && StringUtils.hasLength(readTemplate.getGroupField())) {
            //合并结果
            final String[] fields = readTemplate.getGroupField().split(",");
            List<Map<String, Object>> tempResult = new LinkedList<>();
            Map<String, List<Map<String, Object>>> groupResult = queryResult.stream().collect(
                    Collectors.groupingByConcurrent(
                            (item)-> {
                                StringBuffer key = new StringBuffer();
                                for (String field : fields)
                                    key.append(item.get(field).toString()).append("|");
                                return key.toString();
                            }
                    )
            );
            groupResult.forEach(
                    (key, value)-> {
                        tempResult.add(value.get(0));//只取第一条作为合并结果
                    }
            );

            queryResult = tempResult;
        }
        //判断是否还有下一个节点
        if (message.hasNextNode()) {
            //继续传递
            String node = message.getNextNodes().poll();
            Node nextNode = this.nodeRepository.findFirstByCode(node).orElse(null);
            if (nextNode == null ) {
                logger.error("未找到相应的节点 {}", node);
                if (!message.isAsync())
                    return MessageFactory.successResponse("success", queryResult);
                else return MessageFactory.errorResponse(String.format("未找到相应的节点 %s", node));
            }

            ButterflyMessage newMessage = ButterflyMessage.of(
                    message.getSession(),
                    message.getRequestParam(),
                    TextUtil.writeObjectByFormat(queryResult, readTemplate.getFormat()),// 节点之间的消息传递的格式通过配置文件指定
                    message.getNextNodes(),
                    nextNode.getConfig(),
                    message.isAsync(),
                    readTemplate.getFormat()
            );
            if (newMessage.isAsync()) {//开启异步
                sender.sendMessage(nextNode.getExecutorId(), newMessage);
                return MessageFactory.successResponse("success", queryResult);
            } else {
                //非异步处理
                String executorId = nextNode.getExecutorId();
                IExecutor executor = ExecutorFactory.getExecutor(executorId);
                if (executor != null) {
                    return executor.execute(newMessage);
                } else {
                    return MessageFactory.errorResponse("未找到executor");
                }
            }
        }
        //没有就结束
        if (!message.isAsync())
            return MessageFactory.successResponse("success", queryResult);
        else
            return MessageFactory.successResponse("success", null);
    }

    /**
     * 获取执行器的id {@link KafkaListener#id()}
     * @return 执行器的id
     */
    @Override
    public String getExecutorId() {
        return "butterfly.database.read";
    }
}
