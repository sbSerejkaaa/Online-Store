package com.example.payment.kafka.consumer;


import com.example.core.commandSaga.CreatePaymentCommand;
import com.example.payment.infrastructure.service.payment.ProcessPaymentService;
import com.example.payment.kafka.producer.PaymentTransactionProducer;
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
    private final PaymentTransactionProducer producerEventPayment;

    @KafkaHandler
    public void handleProcessPaymentCommand(
            @Payload CreatePaymentCommand command,
            @Headers Map<String, Object> headers) {


        log.info("📥 [PAYMENT] Получена команда. Order: {}, Amount: {}, Headers: {}",
                command.getOrderId(), command.getAmount(), headers);

        // 1. ИЗВЛЕКАЕМ МЕТАДАННЫЕ
        String correlationId = (String) headers.get("correlationId");
        String commandType = (String) headers.get("commandType");
        String sagaId = (String) headers.get("sagaId");

        // 2. ПРОВЕРЯЕМ ТИП КОМАНДЫ
        if (!"PROCESS_PAYMENT".equals(commandType)) {
            log.warn("Игнорируем команду не-оплаты. Тип: {}", commandType);
            return;
        }

        // 3. ВЫПОЛНЯЕМ БИЗНЕС-ЛОГИКУ ОПЛАТЫ
        try {
            log.info("Обрабатываем оплату заказа: {}", command.getOrderId());

            // 1. ВЫПОЛНЯЕМ СПИСАНИЕ СРЕДСТВ
            paymentService.withdrawForOrder(command);

            // 2. ОТПРАВЛЯЕМ СОБЫТИЕ УСПЕШНОЙ ОПЛАТЫ
            producerEventPayment.publishPaymentCompleted(command, headers, correlationId);

            log.info("✅ [PAYMENT] Оплата успешно обработана. Заказ: {}", command.getOrderId());

        } catch (Exception e) {
            // 3. ОТПРАВЛЯЕМ СОБЫТИЕ ОШИБКИ ОПЛАТЫ
            producerEventPayment.publishPaymentFailed(command, headers, correlationId, e.getMessage());
            log.error("Ошибка оплаты. Заказ: {}, Ошибка: {}",
                    command.getOrderId(), e.getMessage(), e);
        }
    }
}



