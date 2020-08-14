package indi.butterfly.repository;

import indi.butterfly.domain.logging.ExceptionLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 错误日志数据仓库
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.07.26
 * @since 1.0.0
 * @see MongoRepository
 */
@Repository
public interface ExceptionLogRepository extends MongoRepository<ExceptionLog, String> {
}
