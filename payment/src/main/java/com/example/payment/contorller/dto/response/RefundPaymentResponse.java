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
public class RefundPaymentResponse {
    // 1. СТАТУС ВОЗВРАТА
    private ApiPaymentStatus status;
    // Что: Статус обработки возврата средств
    // Значения: SUCCESS, ERROR, PROCESSING
    // Для фронтенда: "Возврат выполнен" / "Ошибка возврата"

    // 2. ЧЕЛОВЕКО-ЧИТАЕМОЕ СООБЩЕНИЕ
    private String message;
    // Что: Понятное описание результата
    // Примеры: "Деньги вернутся на карту в течение 3 дней"
    //           "Возврат средств успешно выполнен"
    // Для фронтенда: Показать зеленое уведомление с этим текстом

    // 3. ВРЕМЯ ОБРАБОТКИ ВОЗВРАТА
    private Instant processedAt;
    // Что: Точное время когда возврат был обработан системой
    // Пример: 2024-01-15T14:30:00Z
    // Для фронтенда: "Запрос на возврат создан 15 января в 14:30"
    // Для поддержки: Точное время операции

    // 4. ИДЕНТИФИКАТОР ВОЗВРАТА
    private UUID refundId;
    // Что: Уникальный ID возврата в вашей системе
    // Для поддержки: "Поиск возврата по ID: 550e8400-e29b-41d4..."
    // Для пользователя: "Номер вашего возврата: #REF-123"

    // 5. ИДЕНТИФИКАТОР ЗАКАЗА
    private UUID orderId;
    // Что: ID заказа по которому делается возврат
    // Для фронтенда: "Возврат по заказу #550e8400-e29b-41d4..."
    // Для связи: Связь возврата с исходным заказом

    // 6. СУММА ВОЗВРАТА
    private BigDecimal amount;
    // Что: Сумма которая будет возвращена пользователю
    // Пример: 5000.00
    // Для фронтенда: "К возврату: 5000 ₽"
    // Для чека: Подтверждение суммы возврата
}
