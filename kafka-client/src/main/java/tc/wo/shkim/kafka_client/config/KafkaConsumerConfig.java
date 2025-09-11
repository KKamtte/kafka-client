package tc.wo.shkim.kafka_client.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tc.wo.shkim.kafka_client.config.properties.KafkaProperties;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaConsumer<String, String> kafkaConsumer() {
        Properties configs = new Properties();
        // 카프카 브로커의 주소 목록은 하나의 브로커가 비정상일 경우 다른 브로커에 연결되어 사용되도록 2개 이상의 ip와 port 를 설정하도록 권장
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // 순서 보장을 위한 추가 설정
        configs.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1); // 한번에 하나의 메시지 처리
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // 수동 커밋
        configs.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed"); // 트랜젝션이 활성화된 producer 에서 commit 된 메시지만 읽음
        return new KafkaConsumer<>(configs);
    }
}
