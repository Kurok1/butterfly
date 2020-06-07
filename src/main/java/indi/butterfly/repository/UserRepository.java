package indi.butterfly.repository;

import indi.butterfly.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    boolean existsByCode(String code);

}
