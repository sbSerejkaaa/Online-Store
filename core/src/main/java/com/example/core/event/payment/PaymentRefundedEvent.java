package com.example.core.event.payment;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class PaymentRefundedEvent {

    UUID eventId;
    Instant createdAt;
    UUID refundId;
    UUID paymentId;
    UUID orderId;
    BigDecimal amount;
    String status;
    String reason;
}
