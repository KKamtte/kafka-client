package tc.wo.shkim.kafka_client.consumer;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerExecutor {

    private final KafkaConsumer<String, String> consumer;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private volatile boolean isRunning = true;
    private final String topic = "my-topic";

    private final AtomicLong processedMessageCount = new AtomicLong(0);
    private final AtomicLong totalProcessingTimeMs = new AtomicLong(0);
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public void start() {
        consumer.subscribe(Collections.singletonList(topic));
        log.info("Kafka Consumer subscribed to topic: {}", topic);
        startTime = LocalDateTime.now();

        executorService.submit(() -> {
            while (isRunning) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                for (ConsumerRecord<String, String> record : records) {
                    long messageStartTime = System.currentTimeMillis();
                    processMessage(record);

                    // 처리 시간 측정
                    long processingTime = System.currentTimeMillis() - messageStartTime;
                    totalProcessingTimeMs.addAndGet(processingTime);
                    processedMessageCount.incrementAndGet();
                }
            }
        });

        if (!isRunning) {
            consumer.close();
            log.info("Kafka Consumer stopped");
        }
    }

    private void processMessage(ConsumerRecord<String, String> record) {
        try {
            log.info("Processed message - partition: {}, offset: {}, key: {}, value: {}", record.partition(), record.offset(), record.key(), record.value());
            Thread.sleep(10); // 처리시간 10ms 로 가정
        } catch (Exception e) {
            log.error("Error processing message: partition={}, offset={}, key={}",
                    record.partition(), record.offset(), record.key(), e);
        }
    }

    @PreDestroy
    public void stop() {
        log.info("Stopping Kafka Consumer");
        isRunning = false;
        isRunning = false;
        endTime = LocalDateTime.now();
    }

    // 총 처리 메시지 수 반환
    public long getProcessedMessageCount() {
        return processedMessageCount.get();
    }

    // 총 처리 시간 반환
    public long getTotalProcessingTimeMs() {
        return totalProcessingTimeMs.get();
    }

    // 평균 처리 시간 계산
    public double getAverageProcessingTimeMs() {
        long count = processedMessageCount.get();
        return count > 0 ? (double) totalProcessingTimeMs.get() / count : 0.0;
    }

    // TPS 계산
    public double getTPS() {
        if (startTime == null || processedMessageCount.get() == 0) {
            return 0.0;
        }

        LocalDateTime currentEndTime = endTime != null ? endTime : LocalDateTime.now();
        long durationSeconds = Duration.between(startTime, currentEndTime).toSeconds();

        return durationSeconds > 0 ? (double) processedMessageCount.get() / durationSeconds : 0.0;
    }

    public void getPerformanceStats(String name) {
        log.info("=== {} ===", name);
        log.info("메시지 수: {}", getProcessedMessageCount());
        log.info("전체 실행 시간: {} ms", getTotalProcessingTimeMs());
        log.info("평균 실행 시간: {} ms", getAverageProcessingTimeMs());
        log.info("TPS: {}", getTPS());
        log.info("시작 시간: {}", startTime);
        log.info("종료 시간: {}", endTime);
        log.info("============================================");
    }
}
