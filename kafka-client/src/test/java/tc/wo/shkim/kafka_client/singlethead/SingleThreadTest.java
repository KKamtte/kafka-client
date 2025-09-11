package tc.wo.shkim.kafka_client.singlethead;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tc.wo.shkim.kafka_client.consumer.ConsumerExecutor;
import tc.wo.shkim.kafka_client.domain.order.Order;
import tc.wo.shkim.kafka_client.domain.order.enums.OrderStatus;
import tc.wo.shkim.kafka_client.producer.OrderProducerTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class SingleThreadTest {

    @Autowired
    private KafkaProducer<String, String> kafkaProducer;
    @Autowired
    private OrderProducerTest producer;
    @Autowired
    private ConsumerExecutor consumer;

    @Test
    @DisplayName("시나리오1: 기본 처리량 테스트")
    void 기본_처리량_테스트() throws Exception {

        // given: 1000개 테스트 메시지 생성
        long messageCount = 1000L;
        List<Order> orders = generateTestOrders(messageCount);

        // when: 메시지 전송 및 메시지 수신
        CompletableFuture<Void> producerFuture = sendMessageAsync(orders);
        CompletableFuture<Void> consumerFuture = receiveMessageAsync();

        // 메시지 전송
        producerFuture.get(30, TimeUnit.SECONDS);

        // 메시지 수신
        processConsumer(messageCount, 60);
        consumer.stop();
        consumerFuture.get(5, TimeUnit.SECONDS);

        // then: 성능 검증
        assertEquals(messageCount, consumer.getProcessedMessageCount(), "메시지 처리에 손실 발생");
        assertTrue(consumer.getTPS() >= 50.0, "TPS 기대값 이하: " + consumer.getTPS());
        assertTrue(consumer.getAverageProcessingTimeMs() <= 25.0, "처리시간 기대값 이하: " + consumer.getAverageProcessingTimeMs());

        // 성능 통계
        consumer.getPerformanceStats("SingleThread 기본처리량 테스트");
    }

    @Test
    @DisplayName("시나리오2: 부하 테스트")
    void 부하_테스트() throws Exception {
        // given: 10000개 메시지 생성
        long messageCount = 10000L;
        List<Order> orders = generateTestOrders(messageCount);

        // when: 메시지 전송 및 메시지 수신
        CompletableFuture<Void> producerFuture = sendMessageAsync(orders);
        CompletableFuture<Void> consumerFuture = receiveMessageAsync();

        // 메시지 전송
        producerFuture.get(30, TimeUnit.SECONDS);

        // 메시지 수신
        processConsumer(messageCount, 300);
        consumer.stop();
        consumerFuture.get(5, TimeUnit.SECONDS);

        // then: 성능 검증
        assertEquals(messageCount, consumer.getProcessedMessageCount(), "메시지 처리에 손실 발생");
        assertTrue(consumer.getTPS() >= 50.0, "TPS 기대값 이하: " + consumer.getTPS());
        assertTrue(consumer.getAverageProcessingTimeMs() <= 25.0, "처리량 기대값 이하: " + consumer.getAverageProcessingTimeMs());

        // 성능 통계
        consumer.getPerformanceStats("SingleThread 부하처리량 테스트");
    }

    @Test
    @DisplayName("시나리오3: 가변 메시지 테스트")
    void 가변_메시지_테스트() throws Exception {
        // given: 메시지 크기 다양하게 생성
        long messageCount = 1000L;
        List<Order> orders = generateVariableTestOrders(messageCount);

        // when: 메시지 전송 및 메시지 수신
        CompletableFuture<Void> producerFuture = sendMessageAsync(orders);
        CompletableFuture<Void> consumerFuture = receiveMessageAsync();

        // 메시지 전송
        producerFuture.get(30, TimeUnit.SECONDS);

        // 메시지 수신
        processConsumer(messageCount, 60);
        consumer.stop();
        consumerFuture.get(5, TimeUnit.SECONDS);

        // then: 성능 검증
        assertEquals(messageCount, consumer.getProcessedMessageCount(), "메시지 처리에 손실 발생");
        assertTrue(consumer.getTPS() >= 50.0, "TPS 기대값 이하: " + consumer.getTPS());
        assertTrue(consumer.getAverageProcessingTimeMs() <= 25.0, "처리량 기대값 이하: " + consumer.getAverageProcessingTimeMs());

        // 성능 통계
        consumer.getPerformanceStats("SingleThread 가변메시지 처리량 테스트");
    }

    private void processConsumer(long messageCount, int timeoutSeconds) {
        long startTime = System.currentTimeMillis();
        long timeout = timeoutSeconds * 1000L;

        while (System.currentTimeMillis() - startTime < timeout) {
            if (consumer.getProcessedMessageCount() >= messageCount) {
                return;
            }
        }

        System.out.println("메시지 처리 타임아웃");
    }

    private CompletableFuture<Void> receiveMessageAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                consumer.start();
            } catch (Exception e) {
                throw new RuntimeException("Failed to start consumer", e);
            }
        });
    }

    private CompletableFuture<Void> sendMessageAsync(List<Order> orders) {
        return CompletableFuture.runAsync(() -> {
            try {
                for (Order order : orders) {
                    producer.sendOrderMessage(kafkaProducer, order).get();
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to send messages", e);
            }
        });
    }

    private List<Order> generateTestOrders(long messageCount) {
        List<Order> orders = new ArrayList<>();

        for (int i = 0; i < messageCount; i++) {
            Order order = new Order(
                    String.format("event_%03d_001", i),
                    String.format("ORDER_%03d", i),
                    OrderStatus.ORDER_CREATE,
                    LocalDateTime.now(),
                    0,
                    0
            );
            orders.add(order);
        }
        return orders;
    }

    private List<Order> generateVariableTestOrders(long messageCount) {
        List<Order> orders = new ArrayList<>();

        for (int i = 0; i < messageCount; i++) {
            String orderId;
            String eventId;

            int remainder = i % 10;

            if (remainder <= 3) {
                // 30%: 일반 메시지(~300 bytes)
                eventId = String.format("event_%03d_001", i);
                orderId = String.format("ORDER_%03d", i);
            } else if (remainder <= 6) {
                // 30%: 소형 메시지 (~800 bytes)
                eventId = String.format("event_%03d_001_%s", i, "X".repeat(50));
                orderId = String.format("ORDER_%03d_%s", i, "Y".repeat(50));
            } else if (remainder <= 8) {
                // 20%: 중형 메시지 (~1.5KB)
                eventId = String.format("event_%03d_001_%s", i, "X".repeat(200));
                orderId = String.format("ORDER_%03d_%s", i, "Y".repeat(200));
            } else {
                // 20%: 대형 메시지 (~2KB)
                eventId = String.format("event_%03d_001_%s", i, "X".repeat(400));
                orderId = String.format("ORDER_%03d_%s", i, "Y".repeat(400));
            }

            Order order = new Order(
                    eventId,
                    orderId,
                    OrderStatus.ORDER_CREATE,
                    LocalDateTime.now(),
                    0,
                    0
            );
            orders.add(order);
        }
        return orders;
    }
}
