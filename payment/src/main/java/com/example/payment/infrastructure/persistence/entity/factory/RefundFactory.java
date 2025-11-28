package com.example.payment.infrastructure.persistence.entity.factory;

import com.example.payment.infrastructure.persistence.entity.Payment;
import com.example.payment.infrastructure.persistence.entity.Refund;
import com.example.payment.infrastructure.persistence.enums.RefundStatus;
import com.example.payment.application.command.RefundPaymentCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RefundFactory {

    public Refund createFromRefundCommand(RefundPaymentCommand command, Payment payment) {
        return Refund.builder()
                .payment(payment)
                .amount(payment.getAmount())
                .status(RefundStatus.REQUESTED)
                .build();
    }
}
