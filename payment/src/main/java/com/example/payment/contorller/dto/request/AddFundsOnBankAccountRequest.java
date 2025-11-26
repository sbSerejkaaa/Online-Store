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
public class AddFundsOnBankAccountRequest {
    @NotNull(message = "ID пользователя не может быть null")
    private UUID bankAccountId;;

    @NotNull(message = "Сумма пополнения не может равняться 0")
    BigDecimal amount;





}
