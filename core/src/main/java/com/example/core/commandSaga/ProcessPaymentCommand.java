package com.example.core.commandSaga;

import com.example.core.status.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcessPaymentCommand {
    private UUID orderId;
    private UUID customerId;       // ← переименовали!
    private BigDecimal amount;     // ← ДОБАВИЛИ!
    private String description;    // ← ДОБАВИЛИ!
}
