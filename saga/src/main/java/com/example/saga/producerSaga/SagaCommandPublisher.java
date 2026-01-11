package com.example.saga.producerSaga;

import com.example.core.commandCancelSaga.CompensateOrderCommand;
import com.example.core.commandSaga.ConfirmOrderCommand;
import com.example.core.commandSaga.CreatePaymentCommand;
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
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaCommandPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendReserveProduct(ReserveProductCommand command, Map<String, Object> originalHeaders) {
        String correlationId = (String) originalHeaders.get("correlationId");

        Message<ReserveProductCommand> message = MessageBuilder
                .withPayload(command)
                .setHeader(KafkaHeaders.TOPIC, "saga.products.commands")
                .setHeader(KafkaHeaders.KEY, command.getOrderId().toString())
                .setHeader("eventId", UUID.randomUUID())
                .setHeader("commandType", "RESERVE_PRODUCT")
                .setHeader("correlationId", correlationId)
                .setHeader("sourceService", "saga-service")
                .setHeader("createdAt", Instant.now().toString())
                .build();

        kafkaTemplate.send(message);
        log.info("🔄 [SAGA] Sent ReserveProductCommand. Order: {}, Correlation: {}",
                command.getOrderId(), correlationId);
    }

    public void sendProcessPayment(CreatePaymentCommand command, Map<String, Object> originalHeaders) {
        String correlationId = (String) originalHeaders.get("correlationId");

        Message<CreatePaymentCommand> message = MessageBuilder
                .withPayload(command)
                .setHeader(KafkaHeaders.TOPIC, "saga.payments.commands")
                .setHeader(KafkaHeaders.KEY, command.getOrderId().toString())
                .setHeader("eventId", UUID.randomUUID())
                .setHeader("commandType", "PROCESS_PAYMENT")
                .setHeader("correlationId", correlationId)
                .setHeader("sourceService", "saga-service")
                .setHeader("eventTimestamp", Instant.now().toString())
                .build();

        kafkaTemplate.send(message);
        log.info("[SAGA] Sent ProcessPaymentCommand. Order: {}, Amount: {}, Correlation: {}",
                command.getOrderId(), command.getAmount(), correlationId);
    }

    public void sendCancelOrder(CompensateOrderCommand command, Map<String, Object> originalHeaders) {
        String correlationId = (String) originalHeaders.get("correlationId");

        Message<CompensateOrderCommand> message = MessageBuilder
                .withPayload(command)
                .setHeader(KafkaHeaders.TOPIC, "saga.orders.commands")  // тот же топик что для confirm
                .setHeader(KafkaHeaders.KEY, command.getOrderId().toString())
                .setHeader("commandType", "CANCEL_ORDER")  // ← ВАЖНО: другой тип!
                .setHeader("correlationId", correlationId)
                .setHeader("sourceService", "saga-service")
                .setHeader("eventTimestamp", Instant.now().toString())
                .build();

        kafkaTemplate.send(message);
        log.info("🔄 [SAGA] Sent CancelOrderCommand. Order: {}, Reason: {}, Correlation: {}",
                command.getOrderId(), command.getReason(), correlationId);
    }

    public void sendConfirmOrder(ConfirmOrderCommand command, Map<String, Object> originalHeaders) {
        String correlationId = (String) originalHeaders.get("correlationId");

        Message<ConfirmOrderCommand> message = MessageBuilder
                .withPayload(command)
                .setHeader(KafkaHeaders.TOPIC, "saga.orders.commands")
                .setHeader(KafkaHeaders.KEY, command.getOrderId().toString())
                .setHeader("eventId", UUID.randomUUID())
                .setHeader("commandType", "CONFIRM_ORDER")
                .setHeader("correlationId", correlationId)
                .setHeader("sourceService", "saga-service")
                .setHeader("eventTimestamp", Instant.now().toString())
                .build();

        kafkaTemplate.send(message);
        log.info("[SAGA] Sent ConfirmOrderCommand. Order: {}, Correlation: {}",
                command.getOrderId(), correlationId);
    }


}
