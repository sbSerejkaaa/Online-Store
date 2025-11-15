package com.example.core.commandSaga;

import lombok.*;

import java.util.UUID;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveProductCommand {
    private UUID orderId;
    private String productName;
    private Integer quantity;
}
