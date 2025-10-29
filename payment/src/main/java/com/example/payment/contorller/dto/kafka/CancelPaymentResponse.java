package com.example.payment.contorller.dto.kafka;

import com.example.payment.contorller.dto.enums.CommandResultStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelPaymentResponse {
    private CommandResultStatus status;
    private String errorMessage;


}
