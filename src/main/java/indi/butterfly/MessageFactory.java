package indi.butterfly;

/**
 * 消息体工厂
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.05.19
 */
public class MessageFactory {


    public static <T> ResponseMessage<T> errorResponse(String msg) {
        ResponseMessage<T> responseMessage = new ResponseMessage<>();
        responseMessage.setMsg(msg);
        responseMessage.setCode(1);
        responseMessage.setError(true);
        return responseMessage;
    }

    public static <T> ResponseMessage<T> successResponse(String msg, T data) {
        ResponseMessage<T> responseMessage = new ResponseMessage<>();
        responseMessage.setMsg(msg);
        responseMessage.setCode(0);
        responseMessage.setError(false);
        responseMessage.setData(data);
        return responseMessage;
    }

}
