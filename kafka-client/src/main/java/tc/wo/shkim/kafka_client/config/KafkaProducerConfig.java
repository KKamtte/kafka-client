package tc.wo.shkim.kafka_client.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tc.wo.shkim.kafka_client.config.properties.KafkaProperties;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaProducer<String, String> kafkaProducer() {
        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configs.put("acks", kafkaProperties.getProducer().getAcks());
        configs.put("retries", kafkaProperties.getProducer().getRetries());
        configs.put("batch.size", kafkaProperties.getProducer().getBatchSize());
        configs.put("linger.ms", kafkaProperties.getProducer().getLingerMs());
        configs.put("buffer.memory", kafkaProperties.getProducer().getBufferMemory());
        configs.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        configs.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return new KafkaProducer<>(configs);
    }
}
