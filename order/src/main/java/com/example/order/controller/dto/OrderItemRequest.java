package com.example.order.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequest {

    @NotNull(message = "ID товара не может быть пустым")
    private UUID productId;

    @NotNull(message = "Имя продукта не может быть пустым")
    private String productName;

    @NotNull(message = "Количество не может быть пустым")
    @Positive(message = "Количество должно быть положительным числом")
    private Integer quantity;

    @NotNull(message = "Цена не может быть пустой")
    @Positive(message = "Цена должна быть положительной")
    private BigDecimal unitPrice;
}