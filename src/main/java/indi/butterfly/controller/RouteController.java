package indi.butterfly.controller;

import indi.butterfly.Message;
import indi.butterfly.MessageFactory;
import indi.butterfly.ResponseMessage;
import indi.butterfly.domain.Route;
import indi.butterfly.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * api定义controller
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.21
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api")
public class RouteController {

    private final RouteRepository routeRepository;

    @Autowired
    public RouteController(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @GetMapping("/route/{id}")
    public ResponseMessage<Route> getById(HttpServletRequest request, @PathVariable("id") long id) {
        return MessageFactory.successResponse("success", this.routeRepository.findById(id).orElse(null));
    }

    @PostMapping("/route")
    public ResponseMessage<Route> create(@RequestBody Route route) {
        if (StringUtils.isEmpty(route.getAppKey())) {
            return MessageFactory.errorResponse("未指定app");
        }

        if (StringUtils.isEmpty(route.getCode())) {
            return MessageFactory.errorResponse("未指定编码");
        }

        if (this.routeRepository.existsByAppKeyAndCode(route.getAppKey(), route.getCode())) {
            return MessageFactory.errorResponse("路由已存在");
        }

        //生成key
        String key = String.format("%s.%s", route.getAppKey(), route.getCode());
        route.setRouteKey(key);

        return MessageFactory.successResponse("success", this.routeRepository.save(route));
    }

    @PutMapping("/route/{id}")
    public ResponseMessage<Route> update(@RequestBody Route route, @PathVariable("id")long id) {
        if (!this.routeRepository.existsById(id)) {
            return MessageFactory.errorResponse("路由不存在");
        }

        if (StringUtils.isEmpty(route.getAppKey())) {
            return MessageFactory.errorResponse("未指定app");
        }

        if (StringUtils.isEmpty(route.getCode())) {
            return MessageFactory.errorResponse("未指定编码");
        }

        //生成key
        String key = String.format("%s.%s", route.getAppKey(), route.getCode());
        route.setRouteKey(key);

        return MessageFactory.successResponse("success", this.routeRepository.save(route));
    }

    @DeleteMapping("/route/{id}")
    public Message delete(@PathVariable("id") long id) {
        this.routeRepository.deleteById(id);
        return MessageFactory.success();
    }
}
