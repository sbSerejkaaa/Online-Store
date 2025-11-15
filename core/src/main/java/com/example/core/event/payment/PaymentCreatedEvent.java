package com.example.core.event.payment;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
public class PaymentCreatedEvent {
    UUID paymentId;
    UUID orderId;
    UUID customerId;
    BigDecimal amount;
    String currency;
    String status;
    String description;
}
