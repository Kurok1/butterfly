package indi.butterfly.listener;

import indi.butterfly.repository.DatasourceConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * Spring Application启动完成事件监听,完成后用于加载数据源配置中的所有数据库驱动
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.05.20
 * @see ApplicationStartedEvent
 */
@Component
public class ApplicationStartedListener implements ApplicationListener<ApplicationStartedEvent> {

    private final Logger logger = LoggerFactory.getLogger(ApplicationStartedListener.class);

    private final DatasourceConfigRepository datasourceConfigRepository;

    public ApplicationStartedListener(@Autowired DatasourceConfigRepository datasourceConfigRepository) {
        this.datasourceConfigRepository = datasourceConfigRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        List<String> list = datasourceConfigRepository.findAllDrive();
        if (list != null && list.size() > 0) {
            Driver driver= null;
            for (String driverClass : list) {
                try {
                    logger.info("开始注册驱动:{}", driverClass);
                    driver = (Driver) Class.forName(driverClass).newInstance();
                    DriverManager.registerDriver(driver);
                    logger.info("{} 注册完成", driverClass);
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
