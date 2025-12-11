package com.example.core.commandSaga;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ReserveProductCommand {
    private UUID orderId;
    private UUID accountId;
    private UUID userId;
    private String productName;
    private Integer quantity;
}
