package com.example.saga.consumerSaga;

import com.example.core.commandCancelSaga.CompensateOrderCommand;
import com.example.core.commandSaga.ConfirmOrderCommand;
import com.example.core.commandSaga.CreatePaymentCommand;
import com.example.core.commandSaga.ReserveProductCommand;
import com.example.core.event.order.OrderCreatedEvent;
import com.example.core.event.payment.PaymentCompletedEvent;

import com.example.core.event.product.ProductReservedEvent;
import com.example.core.failedEvent.payment.PaymentFailedEvent;
import com.example.saga.producerSaga.SagaCommandPublisher;
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
        "order.event.topic",
        "product.event.topic",
        "payment.event.topic",
        "payment.failed.topic"})
@RequiredArgsConstructor
public class SagaEventConsumer {

    private final SagaCommandPublisher commandPublisher;

    @KafkaHandler
    public void handleOrderCreated(@Payload OrderCreatedEvent event,
                                   @Headers Map<String, Object> headers) {

        try {

            String correlationId = (String) headers.get("correlationId");
            log.info("Создание команды для резервации: {}", event.getOrderId());

            ReserveProductCommand command = ReserveProductCommand.builder()
                    .orderId(event.getOrderId())
                    .userId(event.getUserId())
                    .productName(event.getProductName())
                    .quantity(event.getQuantity())
                    .accountId(event.getAccountId())
                    .build();

            commandPublisher.sendReserveProduct(command, headers);
            log.info("Команда отправлена в кафку: {}", command);
        } catch (Exception e) {
            log.error("Ошибка в handleOrderCreated: {}", event.getOrderId(), e);
            throw e;
        }
    }

    @KafkaHandler
    public void handleProductReserved(@Payload ProductReservedEvent event,
                                      @Headers Map<String, Object> headers) {

        log.info("Создание команды на оплату: {}", event.getOrderId());

        try {
            CreatePaymentCommand command = CreatePaymentCommand.builder()
                    .orderId(event.getOrderId())
                    .customerId(event.getUserId())
                    .amount(event.getTotalAmount())
                    .accountId(event.getAccountId())
                    .build();

            commandPublisher.sendProcessPayment(command, headers);

        } catch (Exception e){
            log.error("Ошибка в handleProductReserved: {}", event.getOrderId(), e);
            throw e;
        }
    }



    @KafkaHandler
    public void handlePaymentProcessed(@Payload PaymentCompletedEvent event,
                                       @Headers Map<String, Object> headers) {

        log.info("Создание команды для подтверждения заказа для Order: {}", event.getOrderId());

        try {
            ConfirmOrderCommand command = ConfirmOrderCommand.builder()
                    .orderId(event.getOrderId())
                    .totalAmount(event.getAmount())
                    .build();

            commandPublisher.sendConfirmOrder(command, headers);
        } catch (Exception e){
            log.error("Ошибка в handlePaymentProcessed: {}", event.getOrderId(), e);
            throw e;
        }

    }

    @KafkaHandler
    public void handlePaymentFailed(@Payload PaymentFailedEvent event,
                                    @Headers Map<String, Object> headers) {

        log.error("Не удалось произвести оплату заказа: {}. Error: {}",
                event.getOrderId(), event.getErrorMessage());

        // Просто отменяем заказ (самая простая компенсация)
        CompensateOrderCommand command = CompensateOrderCommand.builder()
                .orderId(event.getOrderId())
                .reason("Payment failed: " + event.getErrorMessage())
                .build();

        commandPublisher.sendCancelOrder(command, headers);

        log.info("Начатая компенсация: отмена заказа {}", event.getOrderId());
    }



}
