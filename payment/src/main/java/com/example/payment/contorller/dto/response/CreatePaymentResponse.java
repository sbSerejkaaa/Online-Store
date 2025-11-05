package com.example.payment.contorller.dto.response;

import com.example.payment.contorller.dto.enums.ApiPaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentResponse {
    // 1. СТАТУС ОПЕРАЦИИ
    private ApiPaymentStatus status;
    // Что: Общий статус выполнения платежа
    // Значения: SUCCESS, ERROR, PROCESSING
    // Для фронтенда: Определяет что показать пользователю

    // 2. СООБЩЕНИЕ ОБ ОШИБКЕ
    private String errorMessage;
    // Что: Детальное описание ошибки (если статус ERROR)
    // Примеры: "Недостаточно средств", "Счет заблокирован"
    // Для фронтенда: Показать красное уведомление с этим текстом

    // 3. ВРЕМЯ ВЫПОЛНЕНИЯ
    private Instant executedAt;
    // Что: Дата и время когда операция была обработана
    // Пример: 2024-01-15T14:30:00
    // Для фронтенда: Показать "Оплата выполнена 15 января в 14:30"

    // 4. ИДЕНТИФИКАТОР ЗАКАЗА
    private UUID orderId;
    // Что: ID заказа который оплачивали
    // Для фронтенда: "Номер вашего заказа: #550e8400-e29b-41d4..."
    // Для поддержки: Поиск операции в системе

    // 5. СУММА ПЛАТЕЖА
    private BigDecimal amount;
    // Что: Сумма которая была списана
    // Пример: 5000.00
    // Для фронтенда: "Списано: 5000 ₽"
    // Для чека: Подтверждение суммы операции
}
