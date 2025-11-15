package com.example.payment.service.converter;

import com.example.payment.contorller.dto.request.CreatePaymentRequest;
import com.example.payment.contorller.dto.request.RefundPaymentRequest;
import com.example.payment.service.command.CreatePaymentCommand;
import com.example.payment.service.command.RefundPaymentCommand;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * TRANSFORMER (ПРЕОБРАЗОВАТЕЛЬ)
 *
 * НАЗНАЧЕНИЕ: Преобразование между разными слоями приложения
 * - API DTO (для внешнего мира) → Business Command (для внутренней логики)
 * - Изолирует знание о структурах данных между слоями
 *
 * ПРИНЦИП: SINGLE RESPONSIBILITY - только преобразование данных
 */
@Component  // Spring создаст бин и будет управлять им
public class PaymentCommandTransformer {
    /**
     * ПРЕОБРАЗОВАНИЕ: CreatePaymentRequest → CreatePaymentCommand
     *
     * ЧТО ДЕЛАЕТ:
     * 1. Берет "сырые" данные из API
     * 2. Добавляет технические поля (commandId, timestamp)
     * 3. Создает IMMUTABLE команду для бизнес-слоя
     *
     * ПОЧЕМУ НУЖНО:
     * - API DTO имеет сеттеры, валидацию - для внешнего мира
     * - Business Command immutable - для надежности бизнес-логики
     */
    public CreatePaymentCommand toCreatePaymentCommand(CreatePaymentRequest request) {

        // Логируем начало преобразования
        System.out.println("🔄 [TRANSFORMER] Converting API DTO to Business Command");
        System.out.println("   FROM: " + request.getClass().getSimpleName());
        System.out.println("   TO: CreatePaymentCommand");

        // СОЗДАЕМ КОМАНДУ с дополнительными техническими полями
        return CreatePaymentCommand.builder()
                .commandId(UUID.randomUUID())      // Уникальный ID для трейсинга
                .orderId(request.getOrderId())     // ID заказа (копируем из DTO)
                .customerId(request.getCustomerId()) // ID пользователя (копируем из DTO)
                .amount(request.getAmount())       // Сумма (копируем из DTO)
                .description(request.getDescription()) // Описание (копируем из DTO)
                .timestamp(Instant.now())          // Время создания команды (ДОБАВЛЯЕМ!)
                .build();

        // КОМАНДА ГОТОВА! Теперь это immutable объект для бизнес-слоя
    }

    /**
     * ПРЕОБРАЗОВАНИЕ: RefundPaymentRequest → RefundPaymentCommand
     */
    public RefundPaymentCommand toRefundPaymentCommand(RefundPaymentRequest request) {
        System.out.println("🔄 [TRANSFORMER] Converting Refund DTO to Business Command");

        return RefundPaymentCommand.builder()
                .commandId(UUID.randomUUID())      // Уникальный ID
                .orderId(request.getOrderId())     // ID заказа для возврата
                .timestamp(Instant.now())          // Время создания
                .build();
    }
}
