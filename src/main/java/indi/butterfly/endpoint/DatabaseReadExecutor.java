package indi.butterfly.endpoint;

import indi.butterfly.core.ButterflyMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * 数据库读取
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.07
 */
@Service
public class DatabaseReadExecutor implements IExecutor {

    @Override
    @KafkaListener(topics = {"butterfly-database-read"}, id = "butterfly.database.read")
    public void process(ButterflyMessage message) {
        //TODO 执行逻辑
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
