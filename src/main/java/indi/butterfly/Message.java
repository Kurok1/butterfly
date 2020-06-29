package indi.butterfly;

import java.time.LocalDateTime;

/**
 * 消息体
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.05.19
 * @since 1.0.0
 */
public class Message {

    private String msg;

    private Integer code;

    private boolean error;

    private final LocalDateTime time = LocalDateTime.now();

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
