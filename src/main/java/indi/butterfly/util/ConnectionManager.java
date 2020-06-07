package indi.butterfly.util;

import indi.butterfly.domain.DatasourceConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库连接管理器
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.05.20
 */
public class ConnectionManager {

    /**
     * 获取一个数据库连接<br/>
     * 这里不需要单独注册数据库驱动,驱动在应用启动的时候已经注册了{@link indi.butterfly.listener.ApplicationStartedListener#onApplicationEvent(org.springframework.boot.context.event.ApplicationStartedEvent)}
     * @param datasourceConfig 数据库配置
     * @return 返回一个数据库连接
     */
    public static synchronized Connection getConnection(DatasourceConfig datasourceConfig) {
        try {
            if (datasourceConfig == null)
                return null;
            return DriverManager.getConnection(
                    datasourceConfig.getUrl(),
                    datasourceConfig.getUser(),
                    datasourceConfig.getPassword()
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 回收一个数据库连接
     * @param connection 需要关闭的数据库连接
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
