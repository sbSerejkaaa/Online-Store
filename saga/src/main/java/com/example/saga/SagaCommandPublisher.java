package com.example.saga;

import com.example.core.commandSaga.ConfirmOrderCommand;
import com.example.core.commandSaga.ProcessPaymentCommand;
import com.example.core.commandSaga.ReserveProductCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaCommandPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendReserveProduct(ReserveProductCommand command, Map<String, Object> originalHeaders) {
        String correlationId = (String) originalHeaders.get("correlationId");

        Message<ReserveProductCommand> message = MessageBuilder
                .withPayload(command)
                .setHeader(KafkaHeaders.TOPIC, "saga.product.commands")
                .setHeader(KafkaHeaders.KEY, command.getOrderId().toString())
                .setHeader("commandType", "RESERVE_PRODUCT")
                .setHeader("correlationId", correlationId)
                .setHeader("sourceService", "saga-service")
                .setHeader("timestamp", Instant.now().toString())
                .setHeader("originalEventId", originalHeaders.get("eventId"))
                .build();

        kafkaTemplate.send(message);
        log.info("🔄 [SAGA] Sent ReserveProductCommand. Order: {}, Correlation: {}",
                command.getOrderId(), correlationId);
    }

    public void sendProcessPayment(ProcessPaymentCommand command, Map<String, Object> originalHeaders) {
        String correlationId = (String) originalHeaders.get("correlationId");

        Message<ProcessPaymentCommand> message = MessageBuilder
                .withPayload(command)
                .setHeader(KafkaHeaders.TOPIC, "saga.payment.commands")
                .setHeader(KafkaHeaders.KEY, command.getOrderId().toString())
                .setHeader("commandType", "PROCESS_PAYMENT")
                .setHeader("correlationId", correlationId)
                .setHeader("sourceService", "saga-service")
                .setHeader("timestamp", Instant.now().toString())
                .setHeader("originalEventId", originalHeaders.get("eventId"))
                .build();

        kafkaTemplate.send(message);
        log.info("[SAGA] Sent ProcessPaymentCommand. Order: {}, Amount: {}, Correlation: {}",
                command.getOrderId(), command.getAmount(), correlationId);
    }

    public void sendConfirmOrder(ConfirmOrderCommand command, Map<String, Object> originalHeaders) {
        String correlationId = (String) originalHeaders.get("correlationId");

        Message<ConfirmOrderCommand> message = MessageBuilder
                .withPayload(command)
                .setHeader(KafkaHeaders.TOPIC, "saga.order.commands")
                .setHeader(KafkaHeaders.KEY, command.getOrderId().toString())
                .setHeader("commandType", "CONFIRM_ORDER")
                .setHeader("correlationId", correlationId)
                .setHeader("sourceService", "saga-service")
                .setHeader("timestamp", Instant.now().toString())
                .setHeader("originalEventId", originalHeaders.get("eventId"))
                .build();

        kafkaTemplate.send(message);
        log.info("[SAGA] Sent ConfirmOrderCommand. Order: {}, Correlation: {}",
                command.getOrderId(), correlationId);
    }

    /*  public void sendCancelOrder(CancelOrderCommand command, Map<String, Object> originalHeaders) {
        String correlationId = (String) originalHeaders.get("correlationId");

        Message<CancelOrderCommand> message = MessageBuilder
                .withPayload(command)
                .setHeader(KafkaHeaders.TOPIC, "saga.order.commands")
                .setHeader(KafkaHeaders.KEY, command.getOrderId().toString())
                .setHeader("commandType", "CANCEL_ORDER")
                .setHeader("correlationId", correlationId)
                .setHeader("sourceService", "saga-service")
                .setHeader("timestamp", Instant.now().toString())
                .setHeader("originalEventId", originalHeaders.get("eventId"))
                .build();

        kafkaTemplate.send(message);
        log.info("[SAGA] Sent CancelOrderCommand. Order: {}, Correlation: {}",
                command.getOrderId(), correlationId);
    }

     */
}
