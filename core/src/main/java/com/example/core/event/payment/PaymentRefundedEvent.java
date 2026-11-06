package com.example.core.event.payment;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class PaymentRefundedEvent {
    Instant createdAt;
    UUID refundId;
    UUID paymentId;
    UUID orderId;
    BigDecimal amount;
    String reason;
}
