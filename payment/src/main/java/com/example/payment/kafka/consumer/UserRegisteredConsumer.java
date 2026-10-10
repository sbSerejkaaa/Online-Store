package com.example.payment.kafka.consumer;

import com.example.core.event.users.UserRegisteredEvent;
import com.example.payment.infrastructure.service.bankAccount.CreateBankAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredConsumer {

    private final CreateBankAccountService createBankAccountService;

    @KafkaListener(
            topics = "user-registered-topic",
            groupId = "payment-service-group"
    )
    public void handleUserRegistered(@Payload UserRegisteredEvent event,
                                     @Headers Map<String, Object> headers) {

        String correlationId = (String) headers.get("correlationId");
        if (correlationId == null) correlationId = "unknown-" + event.getUserId();
        MDC.put("correlationId", correlationId);

        try {
            log.info("Получено событие регистрации: userId={}, email={}",
                    event.getUserId(), event.getEmail());

            createBankAccountService.createBankAccount(event.getUserId());
            log.info("Банковский счёт создан для userId={}", event.getUserId());

        } catch (Exception e) {
            log.error("Ошибка создания банковского счёта для userId={}: {}",
                    event.getUserId(), e.getMessage(), e);
        } finally {
            MDC.remove("correlationId");
        }
    }
}