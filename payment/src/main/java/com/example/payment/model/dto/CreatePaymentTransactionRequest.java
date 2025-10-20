package com.example.payment.model.dto;

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
public class CreatePaymentTransactionRequest {

    @NotNull
    private UUID sourceBankAccountId;

    private UUID destinationBankAccountId;

    @NotNull
    private BigDecimal amount;
    @NotNull
    private String currency;

    private String description;


}
