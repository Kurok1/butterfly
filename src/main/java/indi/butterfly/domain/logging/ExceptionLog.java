package indi.butterfly.domain.logging;

import indi.butterfly.core.Session;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

/**
 * 错误日志记录,保留时间天数根据配置决定
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.07.26
 * @since 1.0.0
 */
@Document("exception_log")
public class ExceptionLog {

    @Id
    private String id;

    private String message;

    private String stackTrace;

    private LocalDateTime created;

    private String createdBy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public static ExceptionLog of(Session session, Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(stringWriter);
        t.printStackTrace(pw);
        ExceptionLog log = new ExceptionLog();
        log.setCreatedBy(session.getUser());
        log.setCreated(LocalDateTime.now());
        log.setMessage(t.getMessage());
        log.setStackTrace(stringWriter.toString());

        return log;
    }

}
