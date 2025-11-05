package com.example.payment.service.command;

import lombok.Builder;
import lombok.Value;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
@Value
@Builder
public class CreatePaymentCommand {
    // ТЕХНИЧЕСКИЕ ПОЛЯ
    UUID commandId;        // Уникальный ID команды для трейсинга
    Instant timestamp;     // Время создания команды

    // БИЗНЕС-ПОЛЯ (уже провалидированы в DTO)
    UUID orderId;         // ID заказа
    UUID customerId;      // ID пользователя
    BigDecimal amount;    // Сумма платежа
    String description;   // Описание платежа

    /**
     * toString для логирования
     */
    @Override
    public String toString() {
        return String.format("CreatePaymentCommand[commandId=%s, orderId=%s, amount=%s]",
                commandId, orderId, amount);
    }
}
