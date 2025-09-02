package tc.wo.shkim.kafka_client.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tc.wo.shkim.kafka_client.consumer.ConsumerExecutor;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaInitializeConfig {

    private final ConsumerExecutor consumerExecutor;

    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStarted() {
        log.info("Application started, initializing Kafka Consumer");
        consumerExecutor.start();
    }
}