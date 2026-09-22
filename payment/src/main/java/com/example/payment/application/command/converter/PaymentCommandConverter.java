package com.example.payment.application.command.converter;

import com.example.payment.application.command.AddingFundsToYourAccountCommand;
import com.example.payment.application.command.RefundPaymentCommand;
import com.example.payment.web.dto.request.RefundPaymentRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
public class PaymentCommandConverter {

    public AddingFundsToYourAccountCommand toAddAccountCommand(UUID bankAccountId, BigDecimal amount) {
        return AddingFundsToYourAccountCommand.builder()
                .traceId(UUID.randomUUID())
                .timestamp(Instant.now())
                .bankAccountId(bankAccountId)
                .amount(amount)
                .build();
    }

    public RefundPaymentCommand toRefundPaymentCommand(RefundPaymentRequest request) {
        return RefundPaymentCommand.builder()
                .refundTracedId(UUID.randomUUID())
                .orderId(request.getOrderId())
                .timestamp(Instant.now())
                .build();
    }
}