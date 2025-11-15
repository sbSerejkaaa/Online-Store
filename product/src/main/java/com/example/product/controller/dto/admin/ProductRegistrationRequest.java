package com.example.product.controller.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRegistrationRequest {

    @NotNull(message = "Название товара не может быть пустым")
    private String nameProduct;

    @NotNull(message = "Количество товара не может равняться null")
    private Integer quantity;

    @NotNull(message = "Цена не может равняться null")
    private BigDecimal price;
}
