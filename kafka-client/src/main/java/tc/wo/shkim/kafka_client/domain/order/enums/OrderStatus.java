package tc.wo.shkim.kafka_client.domain.order.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    ORDER_CREATED("주문 생성"),
    PAYMENT_REQUESTED("결제 요청"),
    PAYMENT_COMPLETED("결제 완료"),
    PAYMENT_FAILED("결제 실패"),
    SHIPPING_PREPARED("배송 준비"),
    SHIPPING_STARTED("배송 시작"),
    DELIVERY_COMPLETED("배송 완료"),
    ORDER_CONFIRMED("주문 확정"),
    ORDER_CANCELLED("주문 취소");

    private final String name;
}
