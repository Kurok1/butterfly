package indi.butterfly.repository;

import indi.butterfly.domain.Node;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 节点repository
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.05.26
 * @since 1.0.0
 */
@Repository
public interface NodeRepository extends PagingAndSortingRepository<Node, Long> {

    @Query("select * from node where code = :code")
    Optional<Node> getByCode(@Param("code") String code);

    @Query("select * from node where routeId = :routeId order by nodeOrder desc")
    List<Node> findAllByRouteIdOrderByNodeOrderDesc(@Param(":routeId") Long routeId);
}
