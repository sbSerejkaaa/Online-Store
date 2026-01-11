package com.example.payment.kafka.consumer;


import com.example.core.commandSaga.CreatePaymentCommand;
import com.example.payment.infrastructure.service.payment.ProcessPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;


@Slf4j
@Component
@KafkaListener(
        topics = "saga.payments.commands",
        groupId = "payment-service-group"
)
@RequiredArgsConstructor
public class ConsumerPayment {
    private final ProcessPaymentService paymentService;


    @KafkaHandler
    public void handleProcessPaymentCommand(
            @Payload CreatePaymentCommand command,
            @Headers Map<String, Object> headers) {

        log.info("📥 [PAYMENT] Получена команда. Order: {}, Amount: {}",
                command.getOrderId(), command.getAmount());

        String correlationId = (String) headers.get("correlationId");
        String commandType = (String) headers.get("commandType");

        if (!"PROCESS_PAYMENT".equals(commandType)) {
            log.warn("Игнорируем команду не-оплаты. Тип: {}", commandType);
            return;
        }

        try {
            log.info("Обрабатываем оплату заказа: {}", command.getOrderId());

            // 1. ВЫПОЛНЯЕМ СПИСАНИЕ СРЕДСТВ + СОХРАНЕНИЕ В OUTBOX
            // Внутри withdrawForOrder() уже есть outboxService.saveSuccessfulPayment()
            paymentService.withdrawForOrder(command);

            log.info("✅ [PAYMENT] Оплата успешно обработана и сохранена в OUTBOX. Заказ: {}",
                    command.getOrderId());

            // 2. НЕ ОТПРАВЛЯЕМ В KAFKA ЗДЕСЬ!
            // producerEventPayment.publishPaymentCompleted(...); ← УДАЛИТЬ!
            // Отправкой займется OutboxScheduler

        } catch (Exception e) {
            log.error("❌ Ошибка оплаты. Заказ: {}, Ошибка: {}",
                    command.getOrderId(), e.getMessage(), e);

            // 3. Сохраняем ошибку в OUTBOX
            // ProcessPaymentService.handlePaymentFailed() уже делает это
            paymentService.handlePaymentFailed(command, e.getMessage());

            // 4. ПРОБРАСЫВАЕМ исключение для ретрая Kafka
            throw e;
        }
    }
}



