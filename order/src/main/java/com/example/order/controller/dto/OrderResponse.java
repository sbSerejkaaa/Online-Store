package com.example.order.controller.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private UUID orderId;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
    private String status;
    private Instant createdAt;
}