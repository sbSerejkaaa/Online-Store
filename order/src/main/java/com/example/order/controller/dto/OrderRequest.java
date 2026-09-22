package com.example.order.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotNull(message = "ID аккаунта не может быть пустым")
    private UUID accountId;

    @NotEmpty(message = "Заказ не может быть пустым")
    @Valid
    private List<OrderItemRequest> items;
}