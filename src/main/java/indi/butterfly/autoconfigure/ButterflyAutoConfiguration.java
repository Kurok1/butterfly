package indi.butterfly.autoconfigure;

import indi.butterfly.core.AuthFilter;
import indi.butterfly.core.AuthService;
import indi.butterfly.support.ButterflyInfoContributor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestTemplate;

import javax.servlet.DispatcherType;
import java.util.Collections;

/**
 * Butterfly app auto configuration
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.07
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(ButterflyProperties.class)
public class ButterflyAutoConfiguration {

    public static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 15;

    private final ButterflyProperties properties;

    public ButterflyAutoConfiguration(ButterflyProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Autowired
    public FilterRegistrationBean<AuthFilter> authFilterBean(AuthService authService) {
        AuthFilter authFilter = new AuthFilter(authService);

        FilterRegistrationBean<AuthFilter> bean = new FilterRegistrationBean<>();

        bean.setFilter(authFilter);
        bean.setUrlPatterns(Collections.singletonList("/api/*"));
        bean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE);
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);

        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(value = RestTemplate.class)
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    @Bean
    @Primary
    @ConditionalOnMissingBean
    @Order(DEFAULT_ORDER)
    public ButterflyInfoContributor butterflyInfoContributor() {
        return new ButterflyInfoContributor(this.properties);
    }
}
