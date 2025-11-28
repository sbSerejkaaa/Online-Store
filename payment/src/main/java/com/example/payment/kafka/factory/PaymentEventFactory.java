package com.example.payment.kafka.factory;

import com.example.core.event.payment.PaymentCreatedEvent;
import com.example.core.event.payment.PaymentFailedEvent;
import com.example.core.event.payment.PaymentRefundedEvent;

import com.example.payment.infrastructure.persistence.entity.Payment;
import com.example.payment.infrastructure.persistence.entity.Refund;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

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

    public PaymentRefundedEvent createPaymentRefundedEvent(Refund refund) {
        return PaymentRefundedEvent.builder()
                .createdAt(Instant.now())
                .refundId(refund.getId())
                .paymentId(refund.getPayment().getId())
                .orderId(refund.getPayment().getOrderId())
                .amount(refund.getAmount())
                .reason("Customer request")
                .build();
    }

    public PaymentFailedEvent createPaymentFailedEvent(UUID orderId, String operation,
                                                       String errorCode, String errorMessage, UUID customerId) {
        return PaymentFailedEvent.builder()


                .orderId(orderId)

                .customerId(customerId)
                .build();
    }

}
