package indi.butterfly.autoconfigure;

import indi.butterfly.core.ButterflyMessage;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;

/**
 * //TODO
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.07
 */
@Configuration
@EnableConfigurationProperties(ButterflyProperties.class)
public class ButterflyAutoConfiguration {

}
