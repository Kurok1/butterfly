package indi.butterfly.repository;

import indi.butterfly.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;
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

    boolean existsByCode(String code);

    Optional<User> findFirstByCodeAndPassword(String code, String password);

}
