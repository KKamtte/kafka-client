package tc.wo.shkim.kafka_client.domain.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tc.wo.shkim.kafka_client.domain.order.enums.OrderStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Order {
    private String eventId;
    private String orderId;
    private OrderStatus orderStatus;
    private LocalDateTime createTime;
    private Integer priority = 0;
    private Integer retryCount = 0;
}
