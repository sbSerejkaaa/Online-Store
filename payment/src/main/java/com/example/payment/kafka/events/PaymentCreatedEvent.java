package com.example.payment.kafka.events;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class PaymentCreatedEvent {
    UUID eventId;
    Instant createdAt;


    UUID paymentId;
    UUID orderId;
    UUID customerId;
    BigDecimal amount;
    String currency;
    String status;
    String description;
}
