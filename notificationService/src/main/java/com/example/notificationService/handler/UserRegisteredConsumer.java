package com.example.notificationService.handler;

import com.example.core.UserRegisteredEvent;
import com.example.notificationService.exception.NonRetryableException;
import com.example.notificationService.exception.RetryableException;
import com.example.notificationService.service.EmailService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(topics = "user-registered-topic")
public class UserRegisteredConsumer {

    private final EmailService emailService;
    private final MeterRegistry meterRegistry;

    private Counter successCounter;
    private Counter fatalErrorCounter;
    private Counter retryableErrorCounter;

    @PostConstruct
    public void init() {
        this.successCounter = Counter.builder("user.registration.email.success")
                .description("Успешно отправленные welcome emails")
                .register(meterRegistry);

        this.fatalErrorCounter = Counter.builder("user.registration.email.fatal.errors")
                .description("Фатальные ошибки валидации")
                .register(meterRegistry);

        this.retryableErrorCounter = Counter.builder("user.registration.email.retryable.errors")
                .description("Временные ошибки отправки")
                .register(meterRegistry);
    }

    @KafkaHandler
    public void handleUserRegistration(UserRegisteredEvent event){

        log.info("""
             Получено событие регистрации!
             User ID: {}
             Email: {}
             Время: {}
            """, event.getUserId(), event.getEmail(), event.getEventTimestamp());

        try {
            validateEvent(event);
            emailService.sendWelcomeEmail(event);

            successCounter.increment();
            log.info("Успешно обработан пользователь: {}", event.getUserId());

        } catch (NonRetryableException e) {
            fatalErrorCounter.increment();
            log.error("Фатальная ошибка при обработке пользователя {}: {}",
                    event.getUserId(), e.getMessage());
            throw e;

        } catch (Exception e) {
            retryableErrorCounter.increment();
            log.warn("Временная ошибка при обработке пользователя {}: {}",
                    event.getUserId(), e.getMessage());
            throw new RetryableException("Временная ошибка сервиса", e);
        }

    }

    private void validateEvent(UserRegisteredEvent event) {
        if (event.getUserId() == null) {
            throw new NonRetryableException("User ID не может быть null");
        }

        if (event.getEmail() == null || event.getEmail().trim().isEmpty()) {
            throw new NonRetryableException("Email не может быть пустым");
        }

        if (!isValidEmail(event.getEmail())) {
            throw new NonRetryableException("Невалидный формат email: " + event.getEmail());
        }
    }

    private boolean isValidEmail(String email) {
        // Простая валидация email, можно заменить на библиотеку
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
