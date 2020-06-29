package indi.butterfly.executor;

import indi.butterfly.Message;
import indi.butterfly.MessageFactory;
import indi.butterfly.ResponseMessage;
import indi.butterfly.core.ButterflyMessage;
import indi.butterfly.core.ButterflyMessageSender;
import indi.butterfly.core.XsltService;
import indi.butterfly.domain.Node;
import indi.butterfly.repository.NodeRepository;
import indi.butterfly.template.DatabaseWriteTemplate;
import indi.butterfly.template.XsltTransformTemplate;
import indi.butterfly.util.ExecutorFactory;
import indi.butterfly.util.TextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * XSLT 转换executor
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.16
 * @since 1.0.0
 * @see IExecutor
 */
@Component
public class XsltTransformExecutor implements IExecutor {

    private final Logger logger = LoggerFactory.getLogger(XsltTransformExecutor.class);

    private final XsltService xsltService;

    private XsltTransformTemplate transformTemplate;

    private final ButterflyMessageSender sender;

    private final NodeRepository nodeRepository;

    @Autowired
    public XsltTransformExecutor(XsltService xsltService, ButterflyMessageSender sender, NodeRepository nodeRepository) {
        this.xsltService = xsltService;
        this.sender = sender;
        this.nodeRepository = nodeRepository;
    }

    @Override
    @KafkaListener(topics = {"butterfly-xslt-transform"}, id = "butterfly.xslt.transform")
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

        String xsltCode = transformTemplate.getXsltCode();
        String xml = message.getRequestBody();//只能是xml数据
        if (StringUtils.isEmpty(xml)) {
            logger.warn("没有要处理的数据");
            //直接返回成功
            return MessageFactory.successResponse("success", null);
        }

        String xsltValue = this.xsltService.getXslt(xsltCode);

        if (StringUtils.isEmpty(xsltValue)) {
            logger.error("没有查找到对应的xslt数据: code: {}", xsltCode);
            return MessageFactory.errorResponse(String.format("没有查找到对应的xslt数据: code: %s", xsltCode));
        }

        String transformResult = this.xsltService.transform(xml, xsltValue);

        //判断是否还有下一个节点
        if (message.hasNextNode()) {
            //继续传递
            String node = message.getNextNodes().poll();
            Node nextNode = this.nodeRepository.findFirstByCode(node).orElse(null);
            if (nextNode == null) {
                logger.error("未找到相应的节点 {}", node);
                if (!message.isAsync())
                    return MessageFactory.successResponse("success", transformResult);
                else return MessageFactory.errorResponse(String.format("未找到相应的节点 %s", node));
            }

            ButterflyMessage newMessage = ButterflyMessage.of(
                    message.getSession(),
                    message.getRequestParam(),
                    TextUtil.writeObjectByFormat(transformResult, transformTemplate.getOutputFormat()),// 节点之间的消息传递的格式通过配置文件指定
                    message.getNextNodes(),
                    nextNode.getConfig(),
                    message.isAsync(),
                    transformTemplate.getOutputFormat()
            );
            if (newMessage.isAsync()) {//开启异步
                sender.sendMessage(nextNode.getExecutorId(), newMessage);
                return MessageFactory.successResponse("success", transformResult);
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
            return MessageFactory.successResponse("success", transformResult);
        else
            return MessageFactory.successResponse("success", null);
    }

    @Override
    public String getExecutorId() {
        return "butterfly.xslt.transform";
    }

    @Override
    public Message beforeExecute(ButterflyMessage message) {
        transformTemplate = TextUtil.readJson(message.getConfigJson(), XsltTransformTemplate.class);
        if (transformTemplate == null) {
            logger.error("配置数据错误: {}", message.getConfigJson());
            return MessageFactory.error("读取配置数据错误");
        }

        if (StringUtils.isEmpty(transformTemplate.getXsltCode())) {
            logger.error("未配置xslt数据");
            return MessageFactory.error("未配置xslt数据");
        }

        return MessageFactory.success();
    }
}
