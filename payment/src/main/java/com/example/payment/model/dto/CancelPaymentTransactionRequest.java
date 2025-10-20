package com.example.payment.model.dto;

import jakarta.validation.constraints.Min;
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
public class CancelPaymentTransactionRequest {
    @NotNull(message = "Transaction ID не может быть null")
    private UUID transactionId;
    @NotNull
    @Min(value = 1, message = "Возвращаемая сумма не должна быть null")
    private BigDecimal refundedAmount;
    private String reasons;

}
