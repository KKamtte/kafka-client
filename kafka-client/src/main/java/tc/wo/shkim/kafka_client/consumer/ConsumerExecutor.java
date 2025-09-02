package tc.wo.shkim.kafka_client.consumer;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerExecutor {

    private final KafkaConsumer<String, String> consumer;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private volatile boolean isRunning = true;
    private final String topic = "my-topic";

    public void start() {
        consumer.subscribe(Collections.singletonList(topic));
        log.info("Kafka Consumer subscribed to topic: {}", topic);

        executorService.submit(() -> {
            while (isRunning) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                for (ConsumerRecord<String, String> record : records) {
                    log.info("Received message: {}", record.value());
                }
            }
        });
    }

    @PreDestroy
    public void stop() {
        log.info("Stopping Kafka Consumer");
        isRunning = false;
        consumer.close();
        log.info("Kafka Consumer stopped");
    }
}
