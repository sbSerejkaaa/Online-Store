package com.example.core.event.payment;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCompletedEvent {
    private UUID orderId;
    private UUID paymentId;
    private UUID accountId;
    private BigDecimal amount;
    private Instant completedAt;
}