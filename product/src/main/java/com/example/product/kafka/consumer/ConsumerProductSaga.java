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
        topics = "saga.products.commands",
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
            log.warn("Тип команды не соответствует методу резервации : {}", commandType);
            return;
        }

        // 3. ВЫПОЛНЯЕМ БИЗНЕС-ЛОГИКУ
        try {

            log.info("Начало резервации товара по заказу: {}", command.getOrderId());

            // 1. РАССЧИТЫВАЕМ СУММУ
            log.info("Расчет суммы для оплаты");
            BigDecimal totalAmount = productService.calculateTotalAmount(
                    command.getProductName(),
                    command.getQuantity()
            );
            log.info("Сумма для оплаты: {}", totalAmount);

            // 2. РЕЗЕРВИРУЕМ ТОВАР
            productService.reserveProduct(command.getProductName(), command.getQuantity());
            log.info("Продукт зарезервирован");

            // 3. ОТЛАДОЧНАЯ ИНФОРМАЦИЯ ПЕРЕД ОТПРАВКОЙ
            log.info("Headers перед отправкой: {}", headers);
            log.info("CorrelationId: {}", correlationId);
            log.info("Command: orderId={}, product={}, quantity={}",
                    command.getOrderId(), command.getProductName(), command.getQuantity());
            log.info("TotalAmount: {}", totalAmount);

            // 4. ПРОВЕРКА correlationId
            if (correlationId == null) {
                log.error("correlationId is NULL! Использую orderId как correlationId");
                correlationId = command.getOrderId().toString();
            }

            // 5. ОТПРАВЛЯЕМ СОБЫТИЕ
            producerEventProduct.publishProductReserved(command, totalAmount, headers, correlationId);
            log.info("✅ Отправка данныx в Producer. Order: {}", command.getOrderId());

        } catch (Exception e) {
            log.error("❌ Ошибка в КОНЦЕ обработки команды. Order: {}", command.getOrderId(), e);

            // Пробуем отправить событие об ошибке
            try {
                producerEventProduct.publishReservationFailed(command, headers, correlationId, e.getMessage());
            } catch (Exception ex) {
                log.error("❌ Не удалось отправить событие об ошибке", ex);
            }

            // Бросаем исключение чтобы Kafka сделал ретрай
            throw e;
        }

    }
}


