package indi.butterfly.repository;

import indi.butterfly.domain.User;
import indi.butterfly.support.BooleanRowMapper;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据仓库
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.21
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    @Query(value = "select count(1) from user where code = :code", rowMapperClass = BooleanRowMapper.class)
    boolean existsByCode(@Param("code") String code);

    @Query("select * from user where code = :code and password = :password")
    Optional<User> getByCodeAndPassword(@Param("code") String code,@Param("password") String password);

}
