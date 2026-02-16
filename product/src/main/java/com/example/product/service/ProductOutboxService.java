package com.example.product.service;

import com.example.core.commandSaga.ReserveProductCommand;
import com.example.product.entity.ProductOutbox;
import com.example.product.repository.ProductOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class ProductOutboxService {
    private final ProductOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    /**
     * Сохраняет событие успешной резервации товара
     */
    public void saveProductReserved(ReserveProductCommand command, UUID productId, BigDecimal totalAmount) {


        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", command.getOrderId().toString());
            payload.put("productName", command.getProductName());
            payload.put("quantity", command.getQuantity());
            payload.put("productId", productId.toString());
            payload.put("totalAmount", totalAmount);
            payload.put("reservedAt", Instant.now().toString());
            payload.put("accountId", command.getAccountId().toString());

            String jsonPayload = objectMapper.writeValueAsString(payload);
            // 🔥🔥🔥 ВОТ СЮДА ВСТАВЛЯЕМ ПРОВЕРКУ
            if (jsonPayload == null || jsonPayload.trim().isEmpty() || jsonPayload.equals("null")) {
                log.error("🔥🔥🔥 CRITICAL: payload is null for order {}", command.getOrderId());
                throw new RuntimeException("Payload is null for order " + command.getOrderId());
            }

            ProductOutbox outbox = ProductOutbox.builder()
                    .eventType("PRODUCT_RESERVED")
                    .aggregateId(command.getOrderId())
                    .payload(jsonPayload)
                    .build();

            outboxRepository.save(outbox);

            log.info("Saved PRODUCT_RESERVED for order: {}", command.getOrderId());

        } catch (Exception e) {
            log.error("Failed to save PRODUCT_RESERVED for order: {}",
                    command.getOrderId(), e);
            throw new RuntimeException("Failed to save outbox event", e);
        }
    }


       /**
     * Сохраняет событие освобождения резервации
     */
       /*
    public void saveReservationReleased(ReleaseProductReservationCommand command) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", command.getOrderId().toString());
            payload.put("reason", command.getReason());
            payload.put("releasedAt", Instant.now().toString());

            String jsonPayload = objectMapper.writeValueAsString(payload);

            ProductOutbox outbox = ProductOutbox.builder()
                    .eventType("PRODUCT_RESERVATION_RELEASED")
                    .aggregateId(command.getOrderId())
                    .payload(jsonPayload)
                    .build();

            outboxRepository.save(outbox);

            log.info("Saved PRODUCT_RESERVATION_RELEASED for order: {}",
                    command.getOrderId());

        } catch (Exception e) {
            log.error("Failed to save reservation release for order: {}",
                    command.getOrderId(), e);
        }
    }
    /*

    /**
     * Сохраняет событие ошибки резервации
     */
    public void saveReservationFailed(ReserveProductCommand command, String errorMessage) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", command.getOrderId().toString());
            payload.put("productName", command.getProductName());
            payload.put("quantity", command.getQuantity());
            payload.put("errorMessage", errorMessage);
            payload.put("failedAt", Instant.now().toString());

            String jsonPayload = objectMapper.writeValueAsString(payload);

            ProductOutbox outbox = ProductOutbox.builder()
                    .eventType("PRODUCT_RESERVATION_FAILED")
                    .aggregateId(command.getOrderId())
                    .payload(jsonPayload)
                    .build();

            outboxRepository.save(outbox);

            log.info("Saved PRODUCT_RESERVATION_FAILED for order: {}",
                    command.getOrderId());

        } catch (Exception e) {
            log.error("Failed to save reservation failure for order: {}",
                    command.getOrderId(), e);
            throw new RuntimeException("Failed to save outbox failure event", e);
        }
    }

}
