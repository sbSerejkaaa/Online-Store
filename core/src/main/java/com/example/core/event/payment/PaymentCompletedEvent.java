package com.example.core.event.payment;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent {

    UUID orderId;
    BigDecimal amount;
    Instant completedAt;
    UUID accountId;

}
