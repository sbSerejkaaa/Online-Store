package com.example.core.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    private UUID productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
}