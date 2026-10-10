package com.example.core.failedEvent.payment;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentFailedEvent {
    private UUID orderId;
    private UUID userId;
    private String errorMessage;
    private Instant failedAt;
}