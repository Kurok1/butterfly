package indi.butterfly.core;

import indi.butterfly.autoconfigure.ButterflyProperties;
import indi.butterfly.util.ExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 发送 {@link indi.butterfly.core.ButterflyMessage 请求报文}给Kafka
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.07
 * @since 1.0.0
 * @see indi.butterfly.core.ButterflyMessage
 * @see KafkaTemplate
 */
@Component
public class ButterflyMessageSender {

    private final static Logger logger = LoggerFactory.getLogger(ButterflyMessageSender.class);

    private final KafkaTemplate<String, ButterflyMessage> kafkaTemplate;

    @Autowired
    public ButterflyMessageSender(KafkaTemplate<String, ButterflyMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String executorId, ButterflyMessage message) {
        ButterflyProperties.ExecutorDefinition definition = ExecutorFactory.getDefinition(executorId);
        if (definition == null)
            logger.error("执行器不存在, id:[{}]", executorId);
        else {
            this.kafkaTemplate.send(definition.getTopic(), 0 , message.getMessageKey(), message);
        }

    }
}
