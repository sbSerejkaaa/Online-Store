package com.example.payment.infrastructure.outbox;


import com.example.core.commandSaga.CreatePaymentCommand;
import com.example.payment.infrastructure.persistence.entity.Payment;
import com.example.payment.infrastructure.persistence.outboxEntity.PaymentOutbox;
import com.example.payment.infrastructure.persistence.repository.PaymentOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)  // Должен вызываться внутри существующей транзакции
public class PaymentOutboxService {
    private final PaymentOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    /**
     * Сохраняет успешный платеж в outbox
     */
    public void saveSuccessfulPayment(CreatePaymentCommand command, Payment payment) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", command.getOrderId().toString());
            payload.put("amount", command.getAmount());
            payload.put("accountId", command.getAccountId().toString());
            payload.put("paymentId", payment.getId().toString());
            payload.put("completedAt", java.time.Instant.now().toString());


            String jsonPayload = objectMapper.writeValueAsString(payload);

            PaymentOutbox outbox = PaymentOutbox.builder()
                    .eventType("PAYMENT_COMPLETED")
                    .aggregateId(command.getOrderId())
                    .payload(jsonPayload)
                    .build();

            outboxRepository.save(outbox);

            log.info("Saved PAYMENT_COMPLETED for order: {}", command.getOrderId());

        } catch (Exception e) {
            log.error("Failed to save successful payment for order: {}",
                    command.getOrderId(), e);
            // Не бросаем исключение, чтобы не откатывать основную транзакцию
        }
    }

    /**
     * Сохраняет неуспешный платеж в outbox
     */
    public void saveFailedPayment(CreatePaymentCommand command, String errorMessage) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", command.getOrderId().toString());
            payload.put("amount", command.getAmount());
            payload.put("errorMessage", errorMessage);
            payload.put("failedAt", java.time.Instant.now().toString());

            if (command.getAccountId() != null) {
                payload.put("accountId", command.getAccountId().toString());
            }


            String jsonPayload = objectMapper.writeValueAsString(payload);

            PaymentOutbox outbox = PaymentOutbox.builder()
                    .eventType("PAYMENT_FAILED")
                    .aggregateId(command.getOrderId())
                    .payload(jsonPayload)
                    .build();

            outboxRepository.save(outbox);

            log.info("Saved PAYMENT_FAILED for order: {}, error: {}",
                    command.getOrderId(), errorMessage);

        } catch (Exception e) {
            log.error("Failed to save failed payment for order: {}",
                    command.getOrderId(), e);
        }
    }
}
