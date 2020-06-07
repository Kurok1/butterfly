package indi.butterfly.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jdbc.repository.config.JdbcConfiguration;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * 修改JDBC repository命名策略配置,用于覆盖{@code org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration.SpringBootJdbcConfiguration}
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.05.21
 * @see PropertyNamingStrategy
 * @see org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration
 */
@Configuration
public class NamingStrategyJdbcConfiguration extends JdbcConfiguration {

    @Override
    @Bean
    @Primary
    protected RelationalMappingContext jdbcMappingContext(Optional<NamingStrategy> namingStrategy) {
        //重新生成
        namingStrategy = Optional.of(new PropertyNamingStrategy());

        return super.jdbcMappingContext(namingStrategy);
    }


    static class PropertyNamingStrategy implements NamingStrategy {
        @Override
        public String getColumnName(RelationalPersistentProperty property) {
            Assert.notNull(property, "Property must not be null.");
            //直接返回名称
            return property.getName();
        }
    }
}
