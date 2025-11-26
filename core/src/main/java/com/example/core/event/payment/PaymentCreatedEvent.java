package com.example.core.event.payment;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
public class PaymentCreatedEvent {
    UUID paymentId;      // ID платежа для трейсинга и аудита
    UUID orderId;        // Связь с заказом (ключевое поле)
    UUID customerId;     // Кто платил
    BigDecimal amount;   // Сумма платежа

}
