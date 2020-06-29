package indi.butterfly.util;

import indi.butterfly.domain.DatasourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.Driver;

/**
 * 数据库连接管理器
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.05.20
 * @since 1.0.0
 */
public class ConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

    /**
     * 获取一个数据库连接<br/>
     * 这里不需要单独注册数据库驱动,驱动在应用启动的时候已经注册了{@link indi.butterfly.listener.ApplicationStartedListener#onApplicationEvent(org.springframework.boot.context.event.ApplicationStartedEvent)}
     * @param datasourceConfig 数据库配置
     * @return 返回一个数据库连接
     */
    public static synchronized DataSource getDataSource(DatasourceConfig datasourceConfig) {
        try {
            //初始化数据源
            Driver driver = null;

            driver = (Driver) Class.forName(datasourceConfig.getDriverClass()).newInstance();
            return new SimpleDriverDataSource(driver, datasourceConfig.getUrl(), datasourceConfig.getUser(), datasourceConfig.getPassword());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            logger.error("暂不支持的数据库驱动: {}", datasourceConfig.getDriverClass());
            e.printStackTrace();
            return null;
        }
    }

}
