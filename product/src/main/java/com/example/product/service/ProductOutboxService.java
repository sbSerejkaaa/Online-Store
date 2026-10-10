package com.example.product.service;

import com.example.core.commandSaga.ReserveProductCommand;
import com.example.core.event.product.ProductReservationFailedEvent;
import com.example.core.event.product.ProductReservedEvent;
import com.example.product.entity.ProductOutbox;
import com.example.product.repository.ProductOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductOutboxService {

    private final ProductOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveProductReserved(ReserveProductCommand command,
                                    UUID productId,
                                    BigDecimal unitPrice,
                                    BigDecimal totalAmount) {
        try {
            ProductReservedEvent event = ProductReservedEvent.builder()
                    .orderId(command.getOrderId())
                    .productId(productId)
                    .productName(command.getProductName())
                    .quantity(command.getQuantity())
                    .unitPrice(unitPrice)
                    .totalAmount(totalAmount)
                    .createdAt(Instant.now())
                    .build();

            String payload = objectMapper.writeValueAsString(event);

            ProductOutbox outbox = ProductOutbox.builder()
                    .eventType("PRODUCT_RESERVED")
                    .aggregateId(command.getOrderId())
                    .payload(payload)
                    .correlationId(MDC.get("correlationId"))
                    .build();

            outboxRepository.save(outbox);

            log.info("Saved PRODUCT_RESERVED for order: {}, productId: {}",
                    command.getOrderId(), productId);

        } catch (Exception e) {
            log.error("Failed to save PRODUCT_RESERVED for order: {}",
                    command.getOrderId(), e);
            throw new RuntimeException("Failed to save outbox event", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveReservationFailed(ReserveProductCommand command, String errorMessage) {
        try {
            ProductReservationFailedEvent event = ProductReservationFailedEvent.builder()
                    .orderId(command.getOrderId())
                    .productName(command.getProductName())
                    .productQuantity(command.getQuantity())
                    .errorMessage(errorMessage)
                    .createdAt(Instant.now())
                    .build();

            String payload = objectMapper.writeValueAsString(event);

            ProductOutbox outbox = ProductOutbox.builder()
                    .eventType("PRODUCT_RESERVATION_FAILED")
                    .aggregateId(command.getOrderId())
                    .payload(payload)
                    .correlationId(MDC.get("correlationId"))
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