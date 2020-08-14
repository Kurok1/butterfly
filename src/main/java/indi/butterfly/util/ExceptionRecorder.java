package indi.butterfly.util;

import indi.butterfly.core.Session;
import indi.butterfly.domain.logging.ExceptionLog;
import indi.butterfly.repository.ExceptionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * 错误日志记录
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.07.26
 * @since 1.0.0
 */
@Component
public class ExceptionRecorder {

    private final ExceptionLogRepository repository;

    private final Executor executor = Executors.newFixedThreadPool(3);

    @Autowired
    public ExceptionRecorder(ExceptionLogRepository repository) {
        this.repository = repository;
    }

    public void of(Session session, Throwable t) {
        ExceptionLog log = ExceptionLog.of(session, t);

        Mono.just(log).subscribeOn(Schedulers.fromExecutor(this.executor)).subscribe(new HandleException(this.repository));
    }

    private static class HandleException implements Consumer<ExceptionLog> {

        private final ExceptionLogRepository repository;

        public HandleException(ExceptionLogRepository repository) {
            this.repository = repository;
        }

        @Override
        public void accept(ExceptionLog exceptionLog) {
            this.repository.save(exceptionLog);
        }
    }

}
