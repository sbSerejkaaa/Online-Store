package com.example.core.event.payment;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class PaymentCompletedEvent {

    UUID orderId;
    UUID customerId;
    BigDecimal amount;
    Instant completedAt;

}
