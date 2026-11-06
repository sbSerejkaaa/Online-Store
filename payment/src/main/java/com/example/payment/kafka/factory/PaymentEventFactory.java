package com.example.payment.kafka.factory;

import com.example.core.event.payment.PaymentCreatedEvent;
import com.example.core.event.payment.PaymentRefundedEvent;
import com.example.payment.infrastructure.persistence.entity.Payment;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PaymentEventFactory {

    public PaymentCreatedEvent createPaymentCreatedEvent(Payment payment) {
        return PaymentCreatedEvent.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .customerId(payment.getBankAccount().getCustomerId())
                .amount(payment.getAmount())
                .build();
    }

}