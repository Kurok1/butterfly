package indi.butterfly.repository;

import indi.butterfly.domain.Route;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * api路由数据仓库
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.21
 */
@Repository
public interface RouteRepository extends PagingAndSortingRepository<Route, Long> {

    Optional<Route> getByAppKeyAndRouteKey(String appKey, String routeKey);

    boolean existsByAppKeyAndCode(String appKey, String code);

}
