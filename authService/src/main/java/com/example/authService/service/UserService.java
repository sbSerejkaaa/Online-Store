package com.example.authService.service;

import com.example.authService.dto.UserRegistrationRequest;
import com.example.authService.dto.UserResponse;
import com.example.authService.entity.EntityUsers;
import com.example.authService.repository.UserRepository;
import com.example.core.event.users.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.AuthenticationException;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.errors.TopicAuthorizationException;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private static final String USER_REGISTERED_TOPIC = "user-registered-topic";

    private final RefreshTokenService refreshTokenService;

    public UserService(UserRepository userRepository,
                       KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public UserResponse registeredUser(UserRegistrationRequest request){
        log.info("Регистрация пользователя: {}", request.getEmail());

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Пользователь с электронной почтой: "
                    + request.getEmail() + " уже существует.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        EntityUsers newEntityUsers = new EntityUsers(
                request.getUserName(),
                request.getEmail(),
                encodedPassword
        );

        EntityUsers savedUser = userRepository.save(newEntityUsers);

        sendUserRegisteredEvent(savedUser);

        log.info("Пользователь {} успешно зарегистрирован", savedUser.getEmail());
        return mapToResponse(savedUser);
    }

    public String[] loginUser(String email, String password) {
        EntityUsers user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Неверный пароль");
        }

        String accessToken = jwtService.generateAccessToken(
                user.getId().toString(),
                user.getEmail(),
                user.getRole()
        );
        String refreshToken = jwtService.generateRefreshToken(
                user.getId().toString(),
                user.getEmail(),
                user.getRole()
        );

        refreshTokenService.save(user.getId().toString(), refreshToken);

        return new String[]{accessToken, refreshToken};
    }

    private void sendUserRegisteredEvent(EntityUsers user) {
        try {
            UserRegisteredEvent event = new UserRegisteredEvent(user.getId(), user.getEmail(),
                    user.getUserName(), user.getRegistrationDate()
            );

            // ★ correlationId из MDC или fallback
            String correlationId = MDC.get("correlationId");
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = "auth-" + user.getId();
            }

            Message<UserRegisteredEvent> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, USER_REGISTERED_TOPIC)
                    .setHeader(KafkaHeaders.KEY, user.getId().toString())
                    .setHeader("correlationId", correlationId)
                    .setHeader("eventType", "USER_REGISTERED")
                    .setHeader("sourceService", "auth-service")
                    .build();

            CompletableFuture<SendResult<String, UserRegisteredEvent>> future =
                    kafkaTemplate.send(message);

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