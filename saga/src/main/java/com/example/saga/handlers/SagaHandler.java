package com.example.saga.handlers;

import com.example.core.commandSaga.ConfirmOrderCommand;
import com.example.core.commandSaga.CreatePaymentCommand;
import com.example.core.commandSaga.ReserveProductCommand;
import com.example.core.event.order.OrderCreatedEvent;
import com.example.core.event.payment.PaymentCreatedEvent;
import com.example.core.event.product.ProductReservedEvent;
import com.example.saga.SagaCommandPublisher;
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
@KafkaListener(topics ={
        "${order.event.topic}",
        "${product.event.topic}"})
@RequiredArgsConstructor
public class SagaHandler {

    private final SagaCommandPublisher commandPublisher;

    @KafkaHandler
    public void handleOrderCreated(@Payload OrderCreatedEvent event,
                                   @Headers Map<String, Object> headers) {

        String correlationId = (String) headers.get("correlationId");
        log.info("🎯 [SAGA] Starting saga for order: {}", event.getOrderId());

        ReserveProductCommand command = ReserveProductCommand.builder()
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .productName(event.getProductName())
                .quantity(event.getQuantity())
                .build();

        commandPublisher.sendReserveProduct(command, headers);
    }

    @KafkaHandler
    public void handleProductReserved(@Payload ProductReservedEvent event,
                                      @Headers Map<String, Object> headers) {

        log.info("💰 [SAGA] Product reserved for order: {}", event.getOrderId());

        CreatePaymentCommand command = CreatePaymentCommand.builder()
                .orderId(event.getOrderId())
                .customerId(event.getUserId())
                .amount(event.getTotalAmount())
                .build();

        commandPublisher.sendProcessPayment(command, headers);
    }

    @KafkaHandler
    public void handlePaymentProcessed(@Payload PaymentCreatedEvent event,
                                       @Headers Map<String, Object> headers) {

        log.info("✅ [SAGA] Payment processed for order: {}", event.getOrderId());

        ConfirmOrderCommand command = ConfirmOrderCommand.builder()
                .orderId(event.getOrderId())
                .build();

        commandPublisher.sendConfirmOrder(command, headers);
    }

   /* @KafkaHandler
    public void handleProductReservationFailed(@Payload ProductReservationFailedEvent event,
                                               @Headers Map<String, Object> headers) {

        log.error("❌ [SAGA] Product reservation failed for order: {}", event.getOrderId());

        CancelOrderCommand command = CancelOrderCommand.builder()
                .orderId(event.getOrderId())
                .reason("Product reservation failed: " + event.getReason())
                .build();

        commandPublisher.sendCancelOrder(command, headers);
    }

    */
}
