package com.example.payment.service.domain.factory;

import com.example.payment.model.entity.BankAccount;
import com.example.payment.model.entity.Payment;
import com.example.payment.model.enums.PaymentStatus;
import com.example.payment.service.command.CreatePaymentCommand;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class PaymentFactory {
    /**
     * СОЗДАТЬ PAYMENT ИЗ COMMAND
     */
    public Payment createFromCommand(CreatePaymentCommand command, BankAccount bankAccount) {
        log.debug("🏭 [FACTORY] Creating Payment from command: {}", command.getCommandId());

        return Payment.builder()
                .orderId(command.getOrderId())
                .amount(command.getAmount())
                .description(command.getDescription())
                .bankAccount(bankAccount)
                .status(PaymentStatus.CREATED)
                .build();
    }

}
