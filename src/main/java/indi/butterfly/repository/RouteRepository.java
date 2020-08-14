package indi.butterfly.repository;

import indi.butterfly.domain.Route;
import indi.butterfly.support.BooleanRowMapper;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * api路由数据仓库
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.21
 * @since 1.0.0
 */
@Repository
public interface RouteRepository extends PagingAndSortingRepository<Route, Long> {

    @Query("select * from route where appKey = :appKey and routeKey = :routeKey")
    Optional<Route> getByAppKeyAndRouteKey(@Param("appKey") String appKey,@Param("routeKey") String routeKey);

    @Query(value = "select count(1) from route where appKey = :appKey and routeKey = :routeKey", rowMapperClass = BooleanRowMapper.class)
    boolean existsByAppKeyAndCode(@Param("appKey") String appKey,@Param("routeKey") String routeKey);

}
