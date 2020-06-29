package indi.butterfly.repository;

import indi.butterfly.domain.Node;
import org.springframework.data.repository.PagingAndSortingRepository;
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

    Optional<Node> findFirstByCode(String code);

    List<Node> findAllByRouteIdOrderByNodeOrderDesc(Long routeId);
}
