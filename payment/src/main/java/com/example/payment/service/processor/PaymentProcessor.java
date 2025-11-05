package com.example.payment.service.processor;

import com.example.payment.service.handler.PaymentCommandHandler;
import com.example.payment.service.handler.exception.PaymentProcessingException;
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
        log.info("⚙️ [PROCESSOR] Processing command: {}", commandType);

        try {
            // 1. НАЙТИ ПОДХОДЯЩИЙ HANDLER
            PaymentCommandHandler<T, R> handler = findHandler(command);

            // 2. ВЫПОЛНИТЬ КОМАНДУ
            log.debug("🎯 [PROCESSOR] Found handler: {}", handler.getClass().getSimpleName());
            R result = handler.handle(command);

            log.info("✅ [PROCESSOR] Command processed successfully: {}", commandType);
            return result;

        } catch (Exception e) {
            log.error("❌ [PROCESSOR] Command processing failed: {}", commandType, e);
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
                    log.debug("🔍 [PROCESSOR] Handler {} can handle: {}",
                            handler.getClass().getSimpleName(), canHandle);
                    return canHandle;
                })
                .findFirst()
                .orElseThrow(() -> {
                    String errorMsg = "No handler found for command: " + command.getClass().getSimpleName();
                    log.error("❌ [PROCESSOR] {}", errorMsg);
                    return new IllegalArgumentException(errorMsg);
                });
    }

    /**
     * ПОЛУЧИТЬ ВСЕ ЗАРЕГИСТРИРОВАННЫЕ HANDLERS (для дебага)
     */
    public void printRegisteredHandlers() {
        log.info("📋 [PROCESSOR] Registered handlers:");
        handlers.forEach(handler ->
                log.info("   - {}", handler.getClass().getSimpleName())
        );
    }
}
