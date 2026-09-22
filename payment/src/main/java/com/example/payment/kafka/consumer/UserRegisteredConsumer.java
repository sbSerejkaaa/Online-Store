package com.example.payment.kafka.consumer;

import com.example.core.event.users.UserRegisteredEvent;
import com.example.payment.infrastructure.service.bankAccount.CreateBankAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredConsumer {

    private final CreateBankAccountService createBankAccountService;

    @KafkaListener(
            topics = "user-registered-topic",
            groupId = "payment-service-group"
    )
    public void handleUserRegistered(@Payload UserRegisteredEvent event) {
        log.info("Получено событие регистрации: userId={}, email={}",
                event.getUserId(), event.getEmail());

        try {
            createBankAccountService.createBankAccount(event.getUserId());
            log.info("Банковский счёт создан для userId={}", event.getUserId());
        } catch (Exception e) {
            log.error("Ошибка создания банковского счёта для userId={}: {}",
                    event.getUserId(), e.getMessage(), e);
        }
    }
}