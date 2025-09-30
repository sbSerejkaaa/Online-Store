package com.example.notificationService.handler;

import com.example.core.UserRegisteredEvent;
import com.example.notificationService.service.EmailService;
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

    @KafkaHandler
    public void handleUserRegistration(UserRegisteredEvent event){
        log.info("""
             Получено событие регистрации!
             User ID: {}
             Email: {}
             Время: {}
            """, event.getUserId(), event.getEmail(), event.getEventTimestamp());

        emailService.sendWelcomeEmail(event);
    }
}
