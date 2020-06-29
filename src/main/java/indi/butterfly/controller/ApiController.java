package indi.butterfly.controller;

import indi.butterfly.MessageFactory;
import indi.butterfly.ResponseMessage;
import indi.butterfly.core.ButterflyMessage;
import indi.butterfly.core.ButterflyMessageSender;
import indi.butterfly.domain.Node;
import indi.butterfly.domain.Route;
import indi.butterfly.executor.IExecutor;
import indi.butterfly.repository.NodeRepository;
import indi.butterfly.repository.RouteRepository;
import indi.butterfly.util.ExecutorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;


/**
 * API入口controller
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.21
 * @since 1.0.0
 */
@RestController
public class ApiController extends BaseController {

    private final RouteRepository routeRepository;

    private final NodeRepository nodeRepository;

    private final ButterflyMessageSender sender;

    @Autowired
    public ApiController(RouteRepository routeRepository, NodeRepository nodeRepository, ButterflyMessageSender sender) {
        this.routeRepository = routeRepository;
        this.nodeRepository = nodeRepository;
        this.sender = sender;
    }

    @PostMapping("/api/butterfly/in")
    public ResponseMessage<Object> in(HttpServletRequest request,
                                      @RequestParam("appKey")String appKey,
                                      @RequestParam("api") String api,
                                      @RequestParam(value = "format", required = false, defaultValue = "JSON")String format,
                                      @RequestBody String body) {
        Route route = this.routeRepository.getByAppKeyAndRouteKey(appKey, api).orElse(null);

        if (route == null)
            return MessageFactory.errorResponse("未定义的api");

        List<Node> nodes = this.nodeRepository.findAllByRouteIdOrderByNodeOrderDesc(route.getId());
        if (nodes == null || nodes.isEmpty())
            return MessageFactory.errorResponse("未定义节点");
        Node node = nodes.get(0);
        Queue<String> nodeQueue = new ArrayBlockingQueue<>(nodes.size() - 1 == 0 ? 1 : nodes.size() - 1);
        if (nodes.size() > 1) {
            for (int index = 1 ; index < nodes.size(); index ++)
                nodeQueue.offer(nodes.get(index).getCode());
        }


        ButterflyMessage message = new ButterflyMessage();
        message.setSession(getSession(request));
        message.setAsync(route.isAsync() == 1);
        message.setRequestParam(new HashMap<>());
        message.setBodyFormat(format);//默认都是json
        message.setRequestBody(body);
        message.setNextNodes(nodeQueue);
        message.setConfigJson(node.getConfig());

        if (route.isAsync() == 1) {
            sender.sendMessage(node.getExecutorId(), message);
            return MessageFactory.successResponse("success", null);
        } else {
            IExecutor executor = ExecutorFactory.getExecutor(node.getExecutorId());
            return executor.execute(message);
        }

    }

}
