package indi.butterfly.core;

import indi.butterfly.util.TextUtil;

import java.io.Serializable;
import java.util.Map;
import java.util.Queue;

/**
 * 应用提交请求报文
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.07
 * @since 1.0.0
 */
public class ButterflyMessage implements Serializable {


    private static final long serialVersionUID = 1087451148911322328L;

    private Map<String, Object> requestParam;//用于外部请求传参

    private String requestBody;//用于节点内部的数据传递, json格式

    private Queue<String> nextNodes;

    private String configJson;

    private boolean isAsync = false;//是否为异步

    private String bodyFormat;

    private Session session;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setRequestParam(Map<String, Object> requestParam) {
        this.requestParam = requestParam;
    }

    public Map<String, Object> getRequestParam() {
        return requestParam;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public Queue<String> getNextNodes() {
        return nextNodes;
    }

    public void setNextNodes(Queue<String> nextNodes) {
        this.nextNodes = nextNodes;
    }

    public String getConfigJson() {
        return configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }

    public boolean hasNextNode() {
        return this.nextNodes != null && this.nextNodes.size() > 0;
    }

    public boolean isAsync() {
        return isAsync;
    }

    public void setAsync(boolean async) {
        isAsync = async;
    }

    public String getMessageKey() {
        return TextUtil.toPassword(this.toString());
    }

    public String getBodyFormat() {
        return bodyFormat;
    }

    public void setBodyFormat(String bodyFormat) {
        this.bodyFormat = bodyFormat;
    }

    @Override
    public String toString() {
        return "ButterflyMessage{" +
                "requestParam='" + requestParam + '\'' +
                ", requestBody='" + requestBody + '\'' +
                ", nextNodes='" + nextNodes + '\'' +
                ", configJson='" + configJson + '\'' +
                '}';
    }

    public static ButterflyMessage of (
            Session session,
            Map<String, Object> requestParam,
            String requestBody,
            Queue<String> nextNodes,
            String configJson,
            boolean isAsync,
            String bodyFormat
    ) {
        ButterflyMessage message = new ButterflyMessage();
        message.setConfigJson(configJson);
        message.setRequestBody(requestBody);
        message.setRequestParam(requestParam);
        message.setAsync(isAsync);
        message.setNextNodes(nextNodes);
        message.setBodyFormat(bodyFormat);
        return message;
    }
}
