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
@Component
public class ButterflyProperties {

    @Min(value = 0)
    private Long loginExpiredSecond;

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

    public static class ExecutorDefinition {
        private String topic; //kafka topic...

        private String id; //executor id

        private String executorClass;

        private String xsltPath; //xslt 转换文件路径

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

        public String getXsltPath() {
            return xsltPath;
        }

        public void setXsltPath(String xsltPath) {
            this.xsltPath = xsltPath;
        }
    }

}
