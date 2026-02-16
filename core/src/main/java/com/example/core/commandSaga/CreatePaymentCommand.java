package com.example.core.commandSaga;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePaymentCommand {
    private UUID orderId;
    private UUID accountId;
    private BigDecimal amount;
    private String productName;
    private Integer quantity;
}
