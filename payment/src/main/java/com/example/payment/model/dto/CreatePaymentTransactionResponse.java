package com.example.payment.model.dto;

import com.example.payment.model.dto.enums.CommandResultStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentTransactionResponse {

    private UUID paymentTransactionId;
    private CommandResultStatus status;
    private String errorMessage;
    private Instant executedAt;
}
