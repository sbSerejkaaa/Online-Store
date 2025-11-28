package com.example.product.kafka.consumer;

import com.example.core.commandSaga.ReserveProductCommand;
import com.example.product.kafka.producer.ProducerEventProduct;
import com.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
@KafkaListener(
        topics = "saga.product.commands",
        groupId = "product-service-group"
)
@RequiredArgsConstructor
public class ConsumerProductSaga {
    private final ProductService productService;
    private final ProducerEventProduct producerEventProduct;

    @KafkaHandler
    public void handleReserveCommand(
            @Payload ReserveProductCommand command,
            @Headers Map<String, Object> headers) {

        // 1. ИЗВЛЕКАЕМ МЕТАДАННЫЕ
        String correlationId = (String) headers.get("correlationId");
        String commandType = (String) headers.get("commandType");
        String sagaId = (String) headers.get("sagaId");

        // 2. ПРОВЕРЯЕМ ТИП КОМАНДЫ
        if (!"RESERVE_PRODUCT".equals(commandType)) {
            log.warn("🟡 [PRODUCT] Ignoring non-reservation command. Type: {}", commandType);
            return;
        }

        // 3. ВЫПОЛНЯЕМ БИЗНЕС-ЛОГИКУ
        try {

            log.info("🔍 [PRODUCT] Starting reservation for order: {}", command.getOrderId());

            // 1. РАССЧИТЫВАЕМ СУММУ
            log.info("🔍 [PRODUCT] Calculating total amount...");
            BigDecimal totalAmount = productService.calculateTotalAmount(
                    command.getProductName(),
                    command.getQuantity()
            );
            log.info("🔍 [PRODUCT] Total amount calculated: {}", totalAmount);

            // 2. РЕЗЕРВИРУЕМ ТОВАР
            log.info("🔍 [PRODUCT] Reserving product...");
            productService.reserveProduct(command.getProductName(), command.getQuantity());
            log.info("🔍 [PRODUCT] Product reserved successfully");

            // 3. ОТПРАВЛЯЕМ СОБЫТИЕ
            log.info("🔍 [PRODUCT] Publishing ProductReservedEvent...");
            producerEventProduct.publishProductReserved(command, totalAmount, headers, correlationId);
            log.info("✅ [PRODUCT] Product reserved successfully. Order: {}", command.getOrderId());

        } catch (Exception e) {
            producerEventProduct.publishReservationFailed(command, headers, correlationId, e.getMessage());
            log.error(" Reservation failed. Order: {}, Error: {}",
                    command.getOrderId(), e.getMessage());
        }

    }

}


