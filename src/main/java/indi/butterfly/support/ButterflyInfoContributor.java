package indi.butterfly.support;

import indi.butterfly.autoconfigure.ButterflyProperties;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * butterfly info 展示
 * {@code http://host:port/actuator/info}
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.07.26
 * @since 1.0.0
 * @see InfoContributor
 */
public class ButterflyInfoContributor implements InfoContributor {

    private final ButterflyProperties properties;

    public ButterflyInfoContributor(ButterflyProperties properties) {
        this.properties = properties;
    }

    /**
     * {
     *     "version" : "app版本",
     *     "loginExpiredSecond": "登录超时时长",
     *     "allowExecutors": "目前支持的executor信息 {@link indi.butterfly.executor.IExecutor}"
     *     "allowDatabase": "目前支持的关系型数据库连接"
     * }
     *
     * @param builder 构建器
     */
    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("version", properties.getServerVersion());
        map.put("loginExpiredSecond", properties.getLoginExpiredSecond());
        map.put("allowExecutors", properties.getAllowExecutors());
        map.put("allowDatabase", properties.getAllowDatabases());
        builder.withDetails(map);
    }
}
