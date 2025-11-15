package com.example.core.event.payment;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class PaymentFailedEvent {
    UUID eventId;
    Instant createdAt;
    UUID orderId;
    String operation;
    String errorCode;
    String errorMessage;
    UUID customerId;
}
