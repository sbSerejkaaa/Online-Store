package com.example.payment.contorller.dto.kafka;

import com.example.payment.contorller.dto.enums.CommandResultStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentTransactionResponse {
    private CommandResultStatus status;
    private String errorMessage;
    private LocalDateTime executedAt;

}
