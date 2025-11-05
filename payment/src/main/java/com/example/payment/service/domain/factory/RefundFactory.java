package com.example.payment.service.domain.factory;

import com.example.payment.model.entity.Payment;
import com.example.payment.model.entity.Refund;
import com.example.payment.model.enums.RefundStatus;
import com.example.payment.service.command.RefundPaymentCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RefundFactory {

    public Refund createFromCommand(RefundPaymentCommand command, Payment payment) {
        return Refund.builder()
                .payment(payment)
                .amount(payment.getAmount())
                .status(RefundStatus.REQUESTED)
                .build();
    }
}
