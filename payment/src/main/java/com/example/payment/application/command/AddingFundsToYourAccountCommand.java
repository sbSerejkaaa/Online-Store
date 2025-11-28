package com.example.payment.application.command;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddingFundsToYourAccountCommand {
    UUID traceId;
    Instant timestamp;

    UUID bankAccountId;
    BigDecimal amount;
}
