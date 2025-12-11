package com.example.order.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotNull(message = "Имя продукта не может быть пустым")
    private String productName;

    @NotNull(message = "Количество не может быть пустым")
    @Positive(message = "Количество должно быть положительным числом")
    private Integer quantity;

    private UUID accountId;


    // TODO: Добавить когда будет авторизация
    // private UUID userId;

}
