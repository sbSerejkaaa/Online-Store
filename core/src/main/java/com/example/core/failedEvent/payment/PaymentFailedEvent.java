package com.example.core.failedEvent.payment;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class PaymentFailedEvent {
    UUID orderId;
    UUID customerId;
    String productName;
    Integer quantity;
    String errorMessage;
}
