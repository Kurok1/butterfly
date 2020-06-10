package indi.butterfly.repository;

import indi.butterfly.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    boolean existsByCode(String code);

    Optional<User> findFirstByCodeAndPassword(String code, String password);

}
