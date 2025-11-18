package com.example.core.event.payment;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class PaymentRefundedEvent {
    Instant createdAt;    //  Нужно - время создания
    UUID refundId;        //  Нужно - ID возврата
    UUID paymentId;       //  Нужно - связь с платежом
    UUID orderId;         //  Нужно - связь с заказом
    BigDecimal amount;    //  Нужно - сумма возврата
    String reason;        //  Нужно - причина возврата
}
