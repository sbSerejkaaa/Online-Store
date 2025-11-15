package com.example.core.event.payment.factory;

import com.example.payment.kafka.events.PaymentCreatedEvent;
import com.example.payment.kafka.events.PaymentFailedEvent;
import com.example.payment.kafka.events.PaymentRefundedEvent;
import com.example.payment.model.entity.Payment;
import com.example.payment.model.entity.Refund;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class PaymentEventFactory {
    public PaymentCreatedEvent createPaymentCreatedEvent(Payment payment) {
        return PaymentCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .createdAt(Instant.now())
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .customerId(payment.getBankAccount().getCustomerId())
                .amount(payment.getAmount())
                .currency("RUB")
                .status("COMPLETED")
                .description(payment.getDescription())
                .build();
    }

    public PaymentRefundedEvent createPaymentRefundedEvent(Refund refund) {
        return PaymentRefundedEvent.builder()
                .eventId(UUID.randomUUID())
                .createdAt(Instant.now())
                .refundId(refund.getId())
                .paymentId(refund.getPayment().getId())
                .orderId(refund.getPayment().getOrderId())
                .amount(refund.getAmount())
                .status("COMPLETED")
                .reason("Customer request")
                .build();
    }

    public PaymentFailedEvent createPaymentFailedEvent(UUID orderId, String operation,
                                                       String errorCode, String errorMessage, UUID customerId) {
        return PaymentFailedEvent.builder()
                .eventId(UUID.randomUUID())
                .createdAt(Instant.now())
                .orderId(orderId)
                .operation(operation)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .customerId(customerId)
                .build();
    }
}
