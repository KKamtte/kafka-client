package tc.wo.shkim.kafka_client.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {

    private final String bootstrapServers;
    private final Template template;
    private final Consumer consumer;
    private final Producer producer;

    @Getter
    @AllArgsConstructor
    public static class Template {
        private final String defaultTopic;
    }

    @Getter
    @AllArgsConstructor
    public static class Consumer {
        private final String groupId;
    }

    @Getter
    @AllArgsConstructor
    public static class Producer {
        private final String acks;
        private final int retries;
        private final int batchSize;
        private final int lingerMs;
        private final int bufferMemory;
    }
}
