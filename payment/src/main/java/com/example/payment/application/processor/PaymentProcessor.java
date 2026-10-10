package com.example.payment.application.processor;

import com.example.payment.application.handler.PaymentCommandHandler;
import com.example.payment.application.processor.exception.PaymentProcessingException;
import com.example.payment.infrastructure.service.exception.AccountBlockedException;
import com.example.payment.infrastructure.service.exception.BankAccountNotFoundException;
import com.example.payment.infrastructure.service.exception.InsufficientFundsException;
import com.example.payment.infrastructure.service.exception.InvalidAmountException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentProcessor {

    private final List<PaymentCommandHandler<?, ?>> handlers;

    @SuppressWarnings("unchecked")
    public <T, R> R handleCommand(T command) {
        String commandType = command.getClass().getSimpleName();

        try {
            PaymentCommandHandler<T, R> handler = findHandler(command);
            return handler.handle(command);

        } catch (AccountBlockedException
                 | BankAccountNotFoundException
                 | InsufficientFundsException
                 | InvalidAmountException e) {
            // ★ Доменные исключения пропускаем наружу — их обработает GlobalExceptionHandler
            log.warn("Бизнес-ошибка при обработке {}: {}", commandType, e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("Техническая ошибка при обработке {}", commandType, e);
            throw new PaymentProcessingException(
                    "Failed to process command: " + commandType, e
            );
        }
    }

    @SuppressWarnings("unchecked")
    private <T, R> PaymentCommandHandler<T, R> findHandler(T command) {
        return (PaymentCommandHandler<T, R>) handlers.stream()
                .filter(handler -> handler.canHandle(command))
                .findFirst()
                .orElseThrow(() -> {
                    String errorMsg = "No handler found for command: " + command.getClass().getSimpleName();
                    log.error("Processor {}", errorMsg);
                    return new IllegalArgumentException(errorMsg);
                });
    }
}