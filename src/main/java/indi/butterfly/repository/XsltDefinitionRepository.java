package indi.butterfly.repository;

import indi.butterfly.domain.XsltDefinition;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


/**
 * xslt_definition 读写仓库
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.16
 * @since 1.0.0
 */
@Repository
public interface XsltDefinitionRepository extends PagingAndSortingRepository<XsltDefinition, Long> {

}
