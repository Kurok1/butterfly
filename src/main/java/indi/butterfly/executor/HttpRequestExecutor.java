package indi.butterfly.executor;

import indi.butterfly.Message;
import indi.butterfly.MessageFactory;
import indi.butterfly.ResponseMessage;
import indi.butterfly.core.ButterflyMessage;
import indi.butterfly.core.ButterflyMessageSender;
import indi.butterfly.domain.Node;
import indi.butterfly.repository.NodeRepository;
import indi.butterfly.template.HttpRequestTemplate;
import indi.butterfly.util.ExecutorFactory;
import indi.butterfly.util.TextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * http请求executor
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.12
 * @since 1.0.0
 * @see IExecutor
 */
@Component
public class HttpRequestExecutor implements IExecutor {

    private final Logger logger = LoggerFactory.getLogger(HttpRequestExecutor.class);

    private final RestTemplate restTemplate;

    private HttpRequestTemplate requestTemplate;

    private final ButterflyMessageSender sender;

    private final NodeRepository nodeRepository;

    @Autowired
    public HttpRequestExecutor(RestTemplate restTemplate, ButterflyMessageSender messageSender, NodeRepository nodeRepository) {
        this.restTemplate = restTemplate;
        this.sender = messageSender;
        this.nodeRepository = nodeRepository;
    }

    @Override
    @KafkaListener(topics = "butterfly-http-request", id = "butterfly.http.request")
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

        Map<String, Object> params = message.getRequestParam();

        if (params != null && params.size() > 0) {
            //替换url的参数
            final StringBuffer url = new StringBuffer(requestTemplate.getUrl());
            params.forEach(
                    (key, value)-> {
                        String tempUrl = null;
                        tempUrl = url.toString().replace(String.format("{%s}", key), value.toString());
                        url.delete(0, url.length() - 1 );
                        url.append(tempUrl);
                    }
            );
            requestTemplate.setUrl(url.toString());
        }
        URI uri = null;
        try {
            uri = new URI(requestTemplate.getUrl());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            logger.error("uri格式错误: {}", result.getMsg());
            return MessageFactory.errorResponse(String.format("uri格式错误: %s", requestTemplate.getUrl()));
        }
        HttpMethod httpMethod = HttpMethod.resolve(requestTemplate.getRequestMethod());
        Assert.notNull(httpMethod, String.format("不支持的http请求方式: %s", requestTemplate.getRequestMethod()));
        ClientHttpResponse httpResponse = restTemplate.execute(uri, httpMethod,
                (request) -> {
                    Map<String, String> headers = this.requestTemplate.getHeaders();
                    if (headers != null && headers.size() > 0) {
                        headers.forEach(
                                (name, value)-> {
                                    if (StringUtils.hasLength(name))
                                        request.getHeaders().add(name, value);
                                }
                        );
                    }
                },
                response -> response
        );

        String responseBody = null;
        try {
            if (httpResponse == null)
                return MessageFactory.errorResponse("http请求失败,没有响应");
            if (httpResponse.getStatusCode() != HttpStatus.OK) {
                return MessageFactory.errorResponse(String.format("http请求失败,状态码: %d", httpResponse.getStatusCode().value()));
            }

            responseBody = StreamUtils.copyToString(httpResponse.getBody(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("读取http响应异常", e);
            return MessageFactory.errorResponse(result.getMsg());
        }

        //发送给下一个节点,或者直接返回
        //判断是否还有下一个节点
        if (message.hasNextNode()) {
            //继续传递
            String node = message.getNextNodes().poll();
            Node nextNode = this.nodeRepository.findFirstByCode(node).orElse(null);
            if (nextNode == null ) {
                logger.error("未找到相应的节点 {}", node);
                if (!message.isAsync())
                    return MessageFactory.successResponse("success", responseBody);
                else return MessageFactory.errorResponse(String.format("未找到相应的节点 %s", node));
            }

            ButterflyMessage newMessage = ButterflyMessage.of(
                    message.getSession(),
                    message.getRequestParam(),
                    responseBody,// 节点之间的消息传递的格式通过配置文件指定
                    message.getNextNodes(),
                    nextNode.getConfig(),
                    message.isAsync(),
                    requestTemplate.getResponseFormat()
            );
            if (newMessage.isAsync()) {
                sender.sendMessage(nextNode.getExecutorId(), newMessage);
                return MessageFactory.successResponse("success", responseBody);
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
            return MessageFactory.successResponse("success", responseBody);
        else
            return MessageFactory.successResponse("success", null);
    }

    @Override
    public String getExecutorId() {
        return "butterfly.http.request";
    }

    @Override
    public Message beforeExecute(ButterflyMessage message) {
        requestTemplate = TextUtil.readJson(message.getConfigJson(), HttpRequestTemplate.class);
        if (requestTemplate == null) {
            logger.error("配置数据错误: {}", message.getConfigJson());
            return MessageFactory.error("读取配置数据错误");
        }

        if (StringUtils.isEmpty(requestTemplate.getUrl())) {
            logger.error("未配置请求路径url: {}", requestTemplate.getUrl());
            return MessageFactory.error("未配置请求路径url");
        }

        requestTemplate.setRequestMethod(requestTemplate.getRequestMethod().toUpperCase().trim());
        if (HttpMethod.resolve(requestTemplate.getRequestMethod()) == null) {
            logger.error("不支持的http请求方式: {}", requestTemplate.getRequestMethod());
            return MessageFactory.error(String.format("不支持的http请求方式: %s", requestTemplate.getRequestMethod()));
        }

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        //设置请求超时时间,默认为30秒
        requestFactory.setConnectTimeout(this.requestTemplate.getRequestTimeout().intValue());
        this.restTemplate.setRequestFactory(requestFactory);

        return MessageFactory.success();
    }
}
