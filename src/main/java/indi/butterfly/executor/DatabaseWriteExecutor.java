package indi.butterfly.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import indi.butterfly.Message;
import indi.butterfly.MessageFactory;
import indi.butterfly.ResponseMessage;
import indi.butterfly.core.ButterflyMessage;
import indi.butterfly.core.ButterflyMessageSender;
import indi.butterfly.core.QueryResultWrapper;
import indi.butterfly.domain.DatasourceConfig;
import indi.butterfly.repository.DatasourceConfigRepository;
import indi.butterfly.repository.NodeRepository;
import indi.butterfly.template.DatabaseWriteTemplate;
import indi.butterfly.util.ConnectionManager;
import indi.butterfly.util.TextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据库写 executor
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.15
 * @since 1.0.0
 * @see IExecutor
 */
@Component
public class DatabaseWriteExecutor implements IExecutor {

    private final Logger logger = LoggerFactory.getLogger(DatabaseReadExecutor.class);

    private final JdbcTemplate jdbcTemplate;

    private final DatasourceConfigRepository datasourceConfigRepository;

    private final ButterflyMessageSender sender;

    private final NodeRepository nodeRepository;

    private NamedParameterJdbcTemplate parameterJdbcTemplate = null;//named参数jdbcTemplate

    private DatabaseWriteTemplate writeTemplate = null;

    public DatabaseWriteExecutor(JdbcTemplate jdbcTemplate, DatasourceConfigRepository datasourceConfigRepository, ButterflyMessageSender sender, NodeRepository nodeRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.datasourceConfigRepository = datasourceConfigRepository;
        this.sender = sender;
        this.nodeRepository = nodeRepository;
    }

    @Override
    @KafkaListener(topics = {"butterfly-database-write"}, id = "butterfly.database.write")
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

        ObjectMapper mapper = TextUtil.getMapper(writeTemplate.getFormat());
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(LinkedList.class, QueryResultWrapper.class);
        LinkedList<QueryResultWrapper> data = null;
        try {
            data = mapper.readValue(message.getRequestBody(), listType);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("读取数据失败: {}", message.getRequestBody().substring(0, message.getRequestBody().length() > 50 ? 50 : message.getRequestBody().length()-1));
            return MessageFactory.errorResponse(e.getMessage());
        }
        if (data == null || data.isEmpty()) {
            logger.error("数据错误");
            return MessageFactory.errorResponse("输入数据错误");
        }
        String sql = writeTemplate.getSql();
        if (writeTemplate.getParamType() == 1) {
            parameterJdbcTemplate = new NamedParameterJdbcTemplate(this.jdbcTemplate);
            if (writeTemplate.isBatchExecute()) {
                List<Map<String, Object>> mapList = data.stream().map(
                        QueryResultWrapper::toMap
                ).collect(Collectors.toList());
                parameterJdbcTemplate.batchUpdate(sql, list2Source(mapList));
            } else {
                for (QueryResultWrapper wrapper : data) {
                    parameterJdbcTemplate.update(sql, new MapSqlParameterSource(wrapper.toMap()));
                }
            }
        } else {
            //不是命名参数
            if (writeTemplate.isBatchExecute()) {
                jdbcTemplate.batchUpdate(sql, new ListBatchPreparedStatementSetter(data));
            } else {
                for (QueryResultWrapper wrapper : data)
                    jdbcTemplate.update(sql, new ListBatchPreparedStatementSetter(Collections.singletonList(wrapper)));
            }
        }

        //直接结束,一般在数据库完成写之后不会有什么后续操作了
        return MessageFactory.successResponse("success", null);

    }

    @Override
    public String getExecutorId() {
        return "butterfly.database.write";
    }

    @Override
    public Message beforeExecute(ButterflyMessage message) {
        //读取配置
        writeTemplate = TextUtil.readJson(message.getConfigJson(), DatabaseWriteTemplate.class);
        if (writeTemplate == null) {
            logger.error("配置数据错误: {}", message.getConfigJson());
            return MessageFactory.error("读取配置数据错误");
        }
        //查找数据源配置
        DatasourceConfig datasourceConfig = this.datasourceConfigRepository.getByCode(writeTemplate.getDatasource()).orElse(null);
        if (datasourceConfig == null) {
            logger.error("未找到相应的数据源: {}", writeTemplate.getDatasource());
            return MessageFactory.error(String.format("未找到相应的数据源 %s", writeTemplate.getDatasource()));
        }
        DataSource dataSource = ConnectionManager.getDataSource(datasourceConfig);
        if (dataSource == null) {
            logger.error("数据库连接错误");
            return MessageFactory.error("数据库连接错误");
        }
        this.jdbcTemplate.setDataSource(dataSource);

        if (StringUtils.isEmpty(writeTemplate.getSql()))
            return MessageFactory.error("sql为空!");

        return MessageFactory.success();
    }

    private static SqlParameterSource[] list2Source(List<Map<String, Object>> data) {
        if (data == null || data.isEmpty())
            return null;
        SqlParameterSource[] sources = new SqlParameterSource[data.size()];
        for (int i = 0; i < data.size(); i++) {
            sources[i] = new MapSqlParameterSource(data.get(i));
        }
        return sources;
    }

    private static class ListBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

        private List<QueryResultWrapper> data = new LinkedList<>();

        public ListBatchPreparedStatementSetter(List<QueryResultWrapper> data) {
            Assert.notNull(data, "error input");
            this.data = data;
        }

        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
           QueryResultWrapper wrapper = this.data.get(i);
           for (QueryResultWrapper.QueryResultElement element : wrapper.getData())
               ps.setObject(element.getIndex(), element.getValue());
        }

        @Override
        public int getBatchSize() {
            return data.size();
        }
    }

}
