package indi.butterfly.repository;

import indi.butterfly.domain.App;
import indi.butterfly.support.BooleanRowMapper;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
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

    @Query(value = "select count(1) from app where key = :key", rowMapperClass = BooleanRowMapper.class)
    boolean existsByKey(@Param("key") String key);

    @Query(value = "select count(1) from app where code = :code", rowMapperClass = BooleanRowMapper.class)
    boolean existsByCode(@Param("code") String code);

    @Query(value = "select * from app where code = :code")
    Optional<App> getByCode(@Param("code") String code);

}
