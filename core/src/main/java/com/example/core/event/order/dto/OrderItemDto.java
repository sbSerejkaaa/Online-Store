package com.example.core.event.order.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private UUID productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
}