package com.example.payment.contorller.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    @NotNull(message = "ID пользователя не может быть null")
    private UUID customerId;

    @NotNull(message = "ID заказа не может быть равно null")
    private UUID orderId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    private String description;



}
