package indi.butterfly.endpoint;

import indi.butterfly.Message;
import indi.butterfly.ResponseMessage;
import indi.butterfly.core.ButterflyMessage;

/**
 * 执行器基本定义
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.07
 * @see DatabaseReadExecutor
 */
public interface IExecutor {

    /**
     * 处理一个消息
     * @param message 要处理的消息
     * @return 执行结果
     */
    ResponseMessage<Object> execute(ButterflyMessage message);

    String getExecutorId();

    /**
     * 根据message进行一些预处理
     * @param message 传入的message
     */
    Message beforeExecute(ButterflyMessage message);

}
