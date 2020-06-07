package indi.butterfly.endpoint;

import indi.butterfly.core.ButterflyMessage;

/**
 * //TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.07
 */
public interface IExecutor {

    void process(ButterflyMessage message);

    String getExecutorId();

}
