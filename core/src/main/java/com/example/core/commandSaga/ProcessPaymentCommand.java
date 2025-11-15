package com.example.core.commandSaga;

import com.example.core.status.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProcessPaymentCommand {
    private UUID orderId;
    private UUID userId;
    private Integer productQuantity;
    private String productName;
    private OrderStatus status;
    private Instant createdAt;
}
