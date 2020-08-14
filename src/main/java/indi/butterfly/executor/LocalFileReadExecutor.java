package indi.butterfly.executor;

import indi.butterfly.Message;
import indi.butterfly.MessageFactory;
import indi.butterfly.ResponseMessage;
import indi.butterfly.core.ButterflyMessage;
import indi.butterfly.core.ButterflyMessageSender;
import indi.butterfly.core.QueryResultWrapper;
import indi.butterfly.domain.Node;
import indi.butterfly.repository.NodeRepository;
import indi.butterfly.template.LocalFileReadTemplate;
import indi.butterfly.util.ExecutorFactory;
import indi.butterfly.util.TextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 本都文件读取executor
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.30
 * @since 1.0.0
 * @see IExecutor
 */
@Component
public class LocalFileReadExecutor implements IExecutor {

    private LocalFileReadTemplate template = null;

    private final Logger logger = LoggerFactory.getLogger(LocalFileReadExecutor.class);

    private File directory = null;

    private File target = null;

    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final ButterflyMessageSender sender;

    private final NodeRepository nodeRepository;

    @Autowired
    public LocalFileReadExecutor(ButterflyMessageSender sender, NodeRepository nodeRepository) {
        this.sender = sender;
        this.nodeRepository = nodeRepository;
    }

    @Override
    @KafkaListener(topics = {"butterfly-localFile-read"}, id = "butterfly.localFile.read")
    public ResponseMessage<Object> execute(ButterflyMessage message) {
        Message result = beforeExecute(message);
        if (result.isError()) {
            //记录日志
            logger.error("预处理发生错误: {}", result.getMsg());
            //记录处理日志
            return MessageFactory.errorResponse(result.getMsg());
        }

        Assert.notNull(directory, "指定目录不能为空");
        Assert.isTrue(directory.exists(), "指定目录必须存在");

        List<QueryResultWrapper> wrappers = null;
        if (StringUtils.isEmpty(this.template.getFileName())) {
            //扫描整个目录下的文件
            File[] files = directory.listFiles();
            if (files == null || files.length <= 0)
                return MessageFactory.successResponse("success", null);
            wrappers = new LinkedList<>();
            for (File file : files) {
                QueryResultWrapper wrapper = readFile(file);
                if (wrapper != null)
                    wrappers.add(wrapper);
            }
        } else {
            QueryResultWrapper wrapper = readFile(this.target);
            wrappers = Collections.singletonList(wrapper);
        }

        if (message.hasNextNode()) {
            //继续传递
            String node = message.getNextNodes().poll();
            Node nextNode = this.nodeRepository.getByCode(node).orElse(null);
            if (nextNode == null ) {
                logger.error("未找到相应的节点 {}", node);
                if (!message.isAsync())
                    return MessageFactory.successResponse("success", wrappers);
                else return MessageFactory.errorResponse(String.format("未找到相应的节点 %s", node));
            }

            ButterflyMessage newMessage = ButterflyMessage.of(
                    message.getSession(),
                    message.getRequestParam(),
                    TextUtil.writeObjectByFormat(wrappers, this.template.getOutputFormat()),// 节点之间的消息传递的格式通过配置文件指定
                    message.getNextNodes(),
                    nextNode.getConfig(),
                    message.isAsync(),
                    this.template.getOutputFormat()
            );
            if (newMessage.isAsync()) {//开启异步
                sender.sendMessage(nextNode.getExecutorId(), newMessage);
                return MessageFactory.successResponse("success", wrappers);
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
            return MessageFactory.successResponse("success", wrappers);
        else
            return MessageFactory.successResponse("success", null);
    }

    private QueryResultWrapper readFile(File targetFile) {
        if (targetFile == null || targetFile.exists()) {
            return null;
        }

        try {
            InputStream in = new FileInputStream(targetFile);
            // 创建字节数组
            byte[] temp = new byte[1024];//默认大小为1024
            int length = 0;
            // 源文件读取一部分内容
            List<QueryResultWrapper.QueryResultElement> readResult = new LinkedList<>();
            //init...
            StringBuffer fileData = new StringBuffer();//先读取文件所有内容到memory
            while ((length = in.read(temp)) != -1) {
                // 目标文件写入一部分内容
                String data = new String(temp, DEFAULT_CHARSET);
                fileData.append(data);
            }
            if (fileData.length() <= 0)
                return null;

            String result = fileData.toString();
            fileData = null;
            if (StringUtils.hasLength(this.template.getRowDelimiter())) {
                String[] rows = result.split(this.template.getRowDelimiter());
                for (String row : rows) {
                    if (StringUtils.hasLength(this.template.getFieldDelimiter())) {
                        String[] fields = row.split(this.template.getFieldDelimiter());
                        int index = 1;
                        for (String field : fields) {
                            QueryResultWrapper.QueryResultElement element = new QueryResultWrapper.QueryResultElement();
                            element.setIndex(index);
                            element.setName("");
                            element.setValue(field);
                            index++;
                            readResult.add(element);
                        }
                    } else {
                        QueryResultWrapper.QueryResultElement element = new QueryResultWrapper.QueryResultElement();
                        element.setIndex(1);
                        element.setName("");
                        element.setValue(row);
                        readResult.add(element);
                    }
                }
            } else {
                //没有指定行符,直接当一整行处理
                if (StringUtils.hasLength(this.template.getFieldDelimiter())) {
                    String[] fields = result.split(this.template.getFieldDelimiter());
                    int index = 1;
                    for (String field : fields) {
                        QueryResultWrapper.QueryResultElement element = new QueryResultWrapper.QueryResultElement();
                        element.setIndex(index);
                        element.setName("");
                        element.setValue(field);
                        index++;
                        readResult.add(element);
                    }
                } else {
                    QueryResultWrapper.QueryResultElement element = new QueryResultWrapper.QueryResultElement();
                    element.setIndex(1);
                    element.setName("");
                    element.setValue(result);
                    readResult.add(element);
                }
            }

            return new QueryResultWrapper(readResult);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getExecutorId() {
        return "butterfly.localFile.read";
    }

    @Override
    public Message beforeExecute(ButterflyMessage message) {
        //读取配置
        template = TextUtil.readJson(message.getConfigJson(), LocalFileReadTemplate.class);
        if (template == null) {
            logger.error("配置数据错误: {}", message.getConfigJson());
            return MessageFactory.error("读取配置数据错误");
        }

        File directory = null;
        File target = null;

        if (StringUtils.hasLength(this.template.getDirectory())) {
            directory = new File(this.template.getDirectory());
        }

        if (StringUtils.hasLength(this.template.getFileName())) {
            target = new File(this.template.getFileName());
        }

        if ((directory == null || !directory.exists()) && (target == null || !target.exists())) {
            return MessageFactory.error("未配置读取路径");
        }

        return MessageFactory.success();
    }
}
