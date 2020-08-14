package indi.butterfly.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置项
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.07
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "butterfly.app")
@Validated
public class ButterflyProperties {

    @Min(value = 0)
    private Long loginExpiredSecond;

    private boolean autoClearEnabled = true;//开启自动清理日志

    private Map<String, LogDefinition> logDefinitions = new HashMap<>();

    private String serverVersion;

    private List<DatabaseDefinition> allowDatabases;

    private Map<String, ExecutorDefinition> allowExecutors = new HashMap<>();

    public List<DatabaseDefinition> getAllowDatabases() {
        return allowDatabases;
    }

    public void setAllowDatabases(List<DatabaseDefinition> allowDatabases) {
        this.allowDatabases = allowDatabases;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public Long getLoginExpiredSecond() {
        return loginExpiredSecond;
    }

    public void setLoginExpiredSecond(Long loginExpiredSecond) {
        this.loginExpiredSecond = loginExpiredSecond;
    }

    public Map<String, ExecutorDefinition> getAllowExecutors() {
        return allowExecutors;
    }

    public void setAllowExecutors(Map<String, ExecutorDefinition> allowExecutors) {
        this.allowExecutors = allowExecutors;
    }

    public boolean isAutoClearEnabled() {
        return autoClearEnabled;
    }

    public void setAutoClearEnabled(boolean autoClearEnabled) {
        this.autoClearEnabled = autoClearEnabled;
    }

    public Map<String, LogDefinition> getLogDefinitions() {
        return logDefinitions;
    }

    public void setLogDefinitions(Map<String, LogDefinition> logDefinitions) {
        this.logDefinitions = logDefinitions;
    }

    /**
     * @since 1.0.0
     */
    public static class DatabaseDefinition {
        private String name;

        private String driverClass;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDriverClass() {
            return driverClass;
        }

        public void setDriverClass(String driverClass) {
            this.driverClass = driverClass;
        }
    }

    /**
     * @since 1.0.0
     */
    public static class ExecutorDefinition {
        private String topic; //kafka topic...

        private String id; //executor id

        private String executorClass;

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getExecutorClass() {
            return executorClass;
        }

        public void setExecutorClass(String executorClass) {
            this.executorClass = executorClass;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    /**
     * @since 1.0.0
     */
    public static class LogDefinition {


        private String logCollectionName;//需要清除的日志文件表

        @Min(value = 1)
        private Long daysToExpired;//设置过期时间,单位为天

        public String getLogCollectionName() {
            return logCollectionName;
        }

        public void setLogCollectionName(String logCollectionName) {
            this.logCollectionName = logCollectionName;
        }

        public Long getDaysToExpired() {
            return daysToExpired;
        }

        public void setDaysToExpired(Long daysToExpired) {
            this.daysToExpired = daysToExpired;
        }
    }

}
