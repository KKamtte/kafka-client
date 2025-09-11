package tc.wo.shkim.kafka_client.domain.order.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    ORDER_CREATE("주문 생성"),
    PAYMENT_COMPLETE("결제 완료"),
    PAYMENT_FAIL("결제 실패"),
    DELIVERY_COMPLETE("배송 완료"),
    ORDER_CONFIRM("주문 확정"),
    ORDER_CANCEL("주문 취소"),
    ;

    private final String name;
}
