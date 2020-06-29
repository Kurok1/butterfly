package indi.butterfly;

/**
 * 消息体 for web controller
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.05.19
 * @since 1.0.0
 */
public class ResponseMessage<T> extends Message {

    private T data = null;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}
