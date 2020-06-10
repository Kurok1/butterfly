package indi.butterfly.repository;

import indi.butterfly.domain.Node;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 节点repository
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.05.26
 */
@Repository
public interface NodeRepository extends PagingAndSortingRepository<Node, Long> {

    Optional<Node> findFirstByCode(String code);

}
