package com.example.payment.model.dto;

import com.example.payment.model.dto.enums.CommandResultStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelPaymentTransactionResponse {
    private Long refundId;
    private CommandResultStatus status;
    private String errorMessage;

}
