package indi.butterfly.repository;

import indi.butterfly.domain.App;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 应用 repository
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.05.26
 * @since 1.0.0
 */
@Repository
public interface AppRepository extends PagingAndSortingRepository<App, Long> {

    boolean existsByKey(String key);

    boolean existsByCode(String code);

    Optional<App> getByCode(String code);

}
