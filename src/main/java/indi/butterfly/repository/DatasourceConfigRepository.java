package indi.butterfly.repository;

import indi.butterfly.domain.DatasourceConfig;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 数据源配置仓库
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.05.20
 * @since 1.0.0
 */
@Repository
public interface DatasourceConfigRepository extends PagingAndSortingRepository<DatasourceConfig, Long> {

    Optional<DatasourceConfig> findFirstByCode(String code);

    @Query("SELECT DISTINCT driverClass FROM datasource_config")
    List<String> findAllDrive();

}
