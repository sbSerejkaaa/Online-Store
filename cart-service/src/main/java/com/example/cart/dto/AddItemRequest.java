package com.example.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddItemRequest {

    @NotNull(message = "ID товара не может быть null")
    private UUID productId;

    @NotNull(message = "Название товара не может быть null")
    private String productName;

    private String imageUrl;

    @NotNull(message = "Цена не может быть null")
    private BigDecimal price;

    @NotNull(message = "Количество не может быть null")
    @Min(value = 1, message = "Количество должно быть не меньше 1")
    private Integer quantity;
}