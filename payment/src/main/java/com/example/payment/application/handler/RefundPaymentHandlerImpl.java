package com.example.payment.application.handler;

import com.example.payment.web.dto.response.RefundPaymentResponse;
import com.example.payment.application.command.RefundPaymentCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class RefundPaymentHandlerImpl implements PaymentCommandHandler<RefundPaymentCommand, RefundPaymentResponse>{


    @Override
    public RefundPaymentResponse handle(RefundPaymentCommand command) {
        return null;
    }

    @Override
    public boolean canHandle(Object command) {
        return false;
    }
}
