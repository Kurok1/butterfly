package indi.butterfly.initializer;

import indi.butterfly.autoconfigure.ButterflyProperties;
import indi.butterfly.core.XsltService;
import indi.butterfly.executor.IExecutor;
import indi.butterfly.util.ExecutorFactory;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Spring Application启动完成事件监听,完成后用于加载数据源配置中的所有数据库驱动
 * 并且加载executor相关信息
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.05.20
 * @since 1.0.0
 * @see ApplicationStartedEvent
 * @see ExecutorFactory
 */
@Component
public class ButterflyInitializer implements ApplicationListener<ApplicationStartedEvent> {

    private final Logger logger = LoggerFactory.getLogger(ButterflyInitializer.class);

    private final ButterflyProperties properties;

    private final XsltService xsltService;

    @Autowired
    public ButterflyInitializer(ButterflyProperties butterflyProperties, XsltService xsltService) {
        this.properties = butterflyProperties;
        this.xsltService = xsltService;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        List<String> list = this.properties.getAllowDatabases().stream().map(
                ButterflyProperties.DatabaseDefinition::getDriverClass
        ).collect(Collectors.toList());
        if (list.size() > 0) {
            Driver driver= null;
            for (String driverClass : list) {
                try {
                    logger.info("开始注册驱动:{}", driverClass);
                    driver = (Driver) Class.forName(driverClass).newInstance();
                    DriverManager.registerDriver(driver);
                    logger.info("{} 注册完成", driverClass);
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                    logger.error("数据库驱动 {} 注册失败", driverClass);
                }
            }
        }

        ExecutorFactory.clear();
        logger.info("开始注册executor");
        //注册executor
        final Map<String, IExecutor> executors = event.getApplicationContext().getBeansOfType(IExecutor.class);
        executors.forEach(
                (name, executor)->{
                    logger.info("注册executor: path:[{}], class:[{}]", executor.getExecutorId(), executor.getClass().getName());
                    ExecutorFactory.addExecutor(executor.getExecutorId(), executor);
                }
        );
        logger.info("注册executor完成");

        logger.info("开始注册executor配置信息");
        Map<String, ButterflyProperties.ExecutorDefinition> definitionMap = this.properties.getAllowExecutors();
        definitionMap.forEach(
                (key, definition)-> {
                    logger.info("注册executor[{}]的配置信息", key);
                    ExecutorFactory.addDefinition(definition.getId(), definition);
                }
        );
        logger.info("注册executor配置信息完成");

        this.xsltService.clearCache();
        logger.info("开始缓存xslt数据");
        this.xsltService.loadXslt();
        logger.info("缓存xslt数据完成");
    }
}
