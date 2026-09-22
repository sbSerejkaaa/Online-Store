package com.example.product.kafka.consumer;

import com.example.core.commandSaga.ReserveProductCommand;
import com.example.product.service.ProductOutboxService;
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
    private final ProductOutboxService outboxService;

    @KafkaHandler
    public void handleReserveCommand(
            @Payload ReserveProductCommand command,
            @Headers Map<String, Object> headers) {

        String correlationId = (String) headers.get("correlationId");
        String commandType = (String) headers.get("commandType");

        if (!"RESERVE_PRODUCT".equals(commandType)) {
            log.warn("Неизвестный тип команды: {}", commandType);
            return;
        }

        log.info("Резервация: orderId={}, product={}, qty={}, correlationId={}",
                command.getOrderId(), command.getProductName(),
                command.getQuantity(), correlationId);

        try {
            BigDecimal unitPrice = productService.calculateUnitPrice(command.getProductName());
            BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(command.getQuantity()));

            productService.reserveProduct(command, unitPrice, totalAmount);

            log.info("Продукт зарезервирован. Order: {}, totalAmount: {}",
                    command.getOrderId(), totalAmount);

        } catch (Exception e) {
            log.error("Ошибка резервации. Order: {}, причина: {}",
                    command.getOrderId(), e.getMessage(), e);

            outboxService.saveReservationFailed(command, e.getMessage());
        }
    }
}