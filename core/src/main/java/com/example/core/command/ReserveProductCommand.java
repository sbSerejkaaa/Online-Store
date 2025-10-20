package com.example.core.command;

import com.example.core.status.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveProductCommand {
    private UUID productId;
    private Integer productQuantity;
    private UUID orderId;
    private String productName;
    private OrderStatus status;
    private Instant createdAt;

}
