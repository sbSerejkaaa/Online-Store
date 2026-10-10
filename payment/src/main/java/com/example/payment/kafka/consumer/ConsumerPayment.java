package com.example.payment.kafka.consumer;

import com.example.core.commandSaga.CreatePaymentCommand;
import com.example.payment.infrastructure.service.payment.ProcessPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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

        String correlationId = (String) headers.get("correlationId");
        if (correlationId == null) correlationId = "unknown-" + command.getOrderId();
        MDC.put("correlationId", correlationId);

        try {
            log.info("Получена команда PROCESS_PAYMENT. Order: {}, UserId: {}, Amount: {}",
                    command.getOrderId(), command.getUserId(), command.getTotalAmount());

            String commandType = (String) headers.get("commandType");

            if (!"PROCESS_PAYMENT".equals(commandType)) {
                log.warn("Игнорируем команду не-оплаты. Тип: {}", commandType);
                return;
            }

            paymentService.withdrawForOrder(command);

            log.info("Оплата успешно обработана и сохранена в OUTBOX. Заказ: {}",
                    command.getOrderId());

        } catch (Exception e) {
            log.error("Ошибка оплаты. Заказ: {}, Ошибка: {}",
                    command.getOrderId(), e.getMessage(), e);

            paymentService.handlePaymentFailed(command, e.getMessage());

        } finally {
            MDC.remove("correlationId");
        }
    }
}