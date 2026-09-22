package com.example.payment.web.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddFundsOnBankAccountRequest {

    @NotNull(message = "Сумма пополнения не может быть пустой")
    @DecimalMin(value = "1.00", message = "Минимальная сумма пополнения — 1 рубль")
    private BigDecimal amount;
}