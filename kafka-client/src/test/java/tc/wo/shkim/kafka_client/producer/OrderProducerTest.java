package tc.wo.shkim.kafka_client.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import tc.wo.shkim.kafka_client.domain.order.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@SpringBootConfiguration
public class OrderProducerTest {

    @Autowired
    private ObjectMapper objectMapper;

    private final String topic = "my-topic";

    public Future<RecordMetadata> sendOrderMessage(KafkaProducer<String, String> producer, Order order) {
        try {
            String message = objectMapper.writeValueAsString(order);
            List<Header> headers = new ArrayList<>();
            headers.add(new RecordHeader("eventId", order.getEventId().getBytes()));

            ProducerRecord<String, String> record = new ProducerRecord<>(
                    topic,
                    null,
                    order.getOrderId(),
                    message,
                    headers
            );

            return producer.send(record, (metadata, exception) -> {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to send order message", e);
        }
    }
}
