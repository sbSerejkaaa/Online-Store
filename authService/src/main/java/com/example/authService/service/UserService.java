package com.example.authService.service;
import com.example.authService.dto.UserRegistrationRequest;
import com.example.authService.dto.UserResponse;
import com.example.authService.entity.EntityUsers;
import com.example.authService.repository.UserRepository;
import com.example.core.event.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.AuthenticationException;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.errors.TopicAuthorizationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;
    private static final String USER_REGISTERED_TOPIC = "user-registered-topic";

    public UserService(UserRepository userRepository, KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public UserResponse registeredUser(UserRegistrationRequest request){
        log.info("Регистрация пользователя: {}", request.getEmail());

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Пользователь с электронной почтой: "
                    + request.getEmail() + " уже существует.");
        }

        EntityUsers newEntityUsers = new EntityUsers(request.getUserName(), request.getEmail(), request.getPassword());
        EntityUsers savedUser = userRepository.save(newEntityUsers);

        sendUserRegisteredEvent(savedUser);

        log.info("Пользователь {} успешно зарегистрирован", savedUser.getEmail());
        return mapToResponse(savedUser);
    }


    private void sendUserRegisteredEvent(EntityUsers user) {
        try {
            UserRegisteredEvent event = new UserRegisteredEvent(user.getId(), user.getEmail(),
                    user.getUserName(), user.getRegistrationDate()
            );

            CompletableFuture<SendResult<String, UserRegisteredEvent>> future =
                    kafkaTemplate.send(USER_REGISTERED_TOPIC, user.getId().toString(), event);
                    future.whenComplete((result, ex) -> {
                        if (ex != null) {
                            if (ex instanceof TimeoutException) {
                                log.error("Таймаут соединения с Kafka");
                            } else if (ex instanceof AuthenticationException) {
                                log.error("Ошибка аутентификации в Kafka");
                            } else if (ex instanceof TopicAuthorizationException) {
                                log.error("Нет прав на запись в топик");
                            } else {
                                log.error("Другая ошибка: {}", ex.getMessage());
                            }
                        } else {
                            log.info("Событие отправлено в топик: {}", USER_REGISTERED_TOPIC);
                            log.info("UserId: {}, Email: {}", user.getId(), user.getEmail());
                        }
                    });

        } catch (Exception e) {
            log.error("Ошибка при отправке события для пользователя {}: {}",
                    user.getEmail(), e.getMessage());
        }
    }

    private UserResponse mapToResponse(EntityUsers user){
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUserName(user.getUserName());
        response.setEmail(user.getEmail());
        response.setRegistrationDate(user.getRegistrationDate());

        return response;
    }
}
