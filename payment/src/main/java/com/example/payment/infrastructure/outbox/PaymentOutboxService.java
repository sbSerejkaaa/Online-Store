package com.example.payment.infrastructure.outbox;

import com.example.core.commandSaga.CreatePaymentCommand;
import com.example.core.event.payment.PaymentCompletedEvent;
import com.example.core.failedEvent.payment.PaymentFailedEvent;
import com.example.payment.infrastructure.persistence.entity.Payment;
import com.example.payment.infrastructure.persistence.outboxEntity.PaymentOutbox;
import com.example.payment.infrastructure.persistence.repository.PaymentOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentOutboxService {

    private final PaymentOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveSuccessfulPayment(CreatePaymentCommand command, Payment payment) {
        try {
            PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                    .orderId(command.getOrderId())
                    .paymentId(payment.getId())
                    .accountId(command.getAccountId())
                    .amount(command.getTotalAmount())
                    .completedAt(Instant.now())
                    .build();

            String payload = objectMapper.writeValueAsString(event);

            PaymentOutbox outbox = PaymentOutbox.builder()
                    .eventType("PAYMENT_COMPLETED")
                    .aggregateId(command.getOrderId())
                    .payload(payload)
                    .build();

            outboxRepository.save(outbox);

            log.info("Saved PAYMENT_COMPLETED for order: {}", command.getOrderId());

        } catch (Exception e) {
            log.error("Failed to save successful payment for order: {}",
                    command.getOrderId(), e);
            throw new RuntimeException("Failed to save outbox event", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailedPayment(CreatePaymentCommand command, String errorMessage) {
        try {
            PaymentFailedEvent event = PaymentFailedEvent.builder()
                    .orderId(command.getOrderId())
                    .accountId(command.getAccountId())
                    .errorMessage(errorMessage)
                    .failedAt(Instant.now())
                    .build();

            String payload = objectMapper.writeValueAsString(event);

            PaymentOutbox outbox = PaymentOutbox.builder()
                    .eventType("PAYMENT_FAILED")
                    .aggregateId(command.getOrderId())
                    .payload(payload)
                    .build();

            outboxRepository.save(outbox);

            log.info("Saved PAYMENT_FAILED for order: {}, error: {}",
                    command.getOrderId(), errorMessage);

        } catch (Exception e) {
            log.error("Failed to save failed payment for order: {}",
                    command.getOrderId(), e);
            throw new RuntimeException("Failed to save outbox failure event", e);
        }
    }
}