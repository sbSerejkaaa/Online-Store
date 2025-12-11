package com.example.core.commandSaga;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmOrderCommand {
    private UUID orderId;
    private BigDecimal totalAmount;

}
