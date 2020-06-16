package indi.butterfly.template;

import java.io.Serializable;
import java.util.Map;

/**
 * http请求配置模板
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.12
 */
public class HttpRequestTemplate implements Serializable {

    private static final long serialVersionUID = 4727905519266151280L;

    /**
     * 请求路径
     */
    private String url;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 额外的http请求头
     */
    private Map<String, String> headers;

    /**
     * 请求相应体的格式
     */
    private String responseFormat;

    /**
     * 请求超时时间,秒为单位,默认为30
     */
    private Long requestTimeout = 30L;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getResponseFormat() {
        return responseFormat;
    }

    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }

    public Long getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Long requestTimeout) {
        this.requestTimeout = requestTimeout;
    }
}
