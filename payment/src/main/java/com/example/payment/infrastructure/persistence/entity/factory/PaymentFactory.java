package com.example.payment.infrastructure.persistence.entity.factory;

import com.example.core.commandSaga.CreatePaymentCommand;
import com.example.payment.infrastructure.persistence.entity.BankAccount;
import com.example.payment.infrastructure.persistence.entity.Payment;
import com.example.payment.infrastructure.persistence.enums.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class PaymentFactory {
    /**
     * СОЗДАТЬ PAYMENT ИЗ COMMAND
     */
    public Payment createFromCommand(CreatePaymentCommand command, BankAccount bankAccount) {


        return Payment.builder()
                .orderId(command.getOrderId())
                .amount(command.getAmount())
                .bankAccount(bankAccount)
                .status(PaymentStatus.CREATED)
                .build();
    }



}
