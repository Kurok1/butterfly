package indi.butterfly.repository;

import indi.butterfly.domain.HttpRequestConfig;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * http请求配置仓库
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.05.26
 */
@Repository
public interface HttpRequestConfigRepository extends PagingAndSortingRepository<HttpRequestConfig, Long> {

    Optional<HttpRequestConfig> findByCode(String code);

}
