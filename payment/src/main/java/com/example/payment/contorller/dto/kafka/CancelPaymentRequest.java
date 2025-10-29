package com.example.payment.contorller.dto.kafka;

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
public class CancelPaymentRequest {
    @NotNull(message = "Transaction ID must not be null")
    private Long transactionId;
    @NotNull(message = "Cancel amount must not be null")
    @Min(value = 1, message = "Cancel amount must be greater than zero")
    private BigDecimal refundedAmount;
    private String reason;

}
