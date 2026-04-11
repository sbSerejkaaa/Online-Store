package com.example.payment.application.processor;

import com.example.payment.application.handler.PaymentCommandHandler;
import com.example.payment.application.processor.exception.PaymentProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentProcessor {
    private final List<PaymentCommandHandler<?, ?>> handlers;

    /**
     * ОБРАБОТКА КОМАНДЫ
     *
     * ПАТТЕРН: DISPATCHER
     * - Автоматически находит нужный handler для команды
     * - Поддерживает любой тип команды (Create, Refund, Cancel, etc.)
     */
    @SuppressWarnings("unchecked")
    public <T, R> R handleCommand(T command) {
        String commandType = command.getClass().getSimpleName();

        try {
            // 1. НАЙТИ ПОДХОДЯЩИЙ HANDLER
            PaymentCommandHandler<T, R> handler = findHandler(command);

            // 2. ВЫПОЛНИТЬ КОМАНДУ
            R result = handler.handle(command);

            return result;

        } catch (Exception e) {
            throw new PaymentProcessingException(
                    "Failed to process command: " + commandType, e
            );
        }
    }

    /**
     * НАЙТИ HANDLER ДЛЯ КОМАНДЫ
     */
    @SuppressWarnings("unchecked")
    private <T, R> PaymentCommandHandler<T, R> findHandler(T command) {
        return (PaymentCommandHandler<T, R>) handlers.stream()
                .filter(handler -> {
                    boolean canHandle = handler.canHandle(command);

                    return canHandle;
                })
                .findFirst()
                .orElseThrow(() -> {
                    String errorMsg = "No handler found for command: " + command.getClass().getSimpleName();
                    log.error("Processor {}", errorMsg);
                    return new IllegalArgumentException(errorMsg);
                });
    }

}
