package tc.wo.shkim.kafka_client.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import tc.wo.shkim.kafka_client.config.properties.KafkaProperties;

@EnableConfigurationProperties({KafkaProperties.class})
@Configuration
public class PropertiesConfig {
}
