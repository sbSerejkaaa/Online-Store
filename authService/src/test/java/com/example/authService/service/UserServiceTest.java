package com.example.authService.service;

import com.example.authService.dto.UserRegistrationRequest;
import com.example.authService.dto.UserResponse;
import com.example.authService.entity.EntityUsers;
import com.example.authService.repository.UserRepository;
import com.example.core.event.users.UserRegisteredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;

    @InjectMocks
    private UserService userService;

    private EntityUsers testUser;

    @BeforeEach
    void setUp() {
        testUser = new EntityUsers("TestUser", "test@example.com", "encoded_password");
        testUser.setId(UUID.randomUUID());
        testUser.setFailedAttempts(0);
        testUser.setLockoutUntil(null);
        testUser.setRole("USER");
    }

    // ТЕСТ 1: Успешный вход
    @Test
    void loginUser_Success() {
        // GIVEN
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", "encoded_password")).thenReturn(true);
        when(jwtService.generateAccessToken(anyString(), anyString(), anyString())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(anyString(), anyString(), anyString())).thenReturn("refresh-token");

        // WHEN
        String[] tokens = userService.loginUser("test@example.com", "password");

        // THEN
        assertEquals("access-token", tokens[0]);
        assertEquals("refresh-token", tokens[1]);
        verify(refreshTokenService, times(1)).save(anyString(), eq("refresh-token"));
        verify(userRepository, times(1)).save(testUser); // сброс счетчика
    }

    // ТЕСТ 2: Неверный пароль (1-я попытка)
    @Test
    void loginUser_WrongPassword_IncrementsFailedAttempts() {
        // GIVEN
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong", "encoded_password")).thenReturn(false);

        // WHEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.loginUser("test@example.com", "wrong");
        });

        // THEN
        assertTrue(exception.getMessage().contains("Неверный email или пароль"));
        assertEquals(1, testUser.getFailedAttempts());
        verify(userRepository, times(1)).save(testUser);
    }

    // ТЕСТ 3: Блокировка аккаунта после 5 попыток
    @Test
    void loginUser_LocksAccountAfterFiveAttempts() {
        // GIVEN
        testUser.setFailedAttempts(4); // 4 предыдущие попытки
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrong", "encoded_password")).thenReturn(false);

        // WHEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.loginUser("test@example.com", "wrong");
        });

        // THEN
        assertTrue(exception.getMessage().contains("Аккаунт заблокирован"));
        assertEquals(0, testUser.getFailedAttempts()); // сбросился
        assertNotNull(testUser.getLockoutUntil()); // установлен бан
        verify(userRepository, times(1)).save(testUser);
    }

    // ТЕСТ 4: Аккаунт уже заблокирован (до попытки входа)
    @Test
    void loginUser_BlockedAccount_ThrowsException() {
        // GIVEN
        testUser.setLockoutUntil(Instant.now().plusSeconds(900)); // бан на 15 минут
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // WHEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.loginUser("test@example.com", "password");
        });

        // THEN
        assertTrue(exception.getMessage().contains("Аккаунт заблокирован"));
        verify(passwordEncoder, never()).matches(anyString(), anyString()); // даже не проверял пароль
    }

    // ТЕСТ 5: Успешная регистрация
    @Test
    void registeredUser_Success() {
        // GIVEN
        UserRegistrationRequest request = new UserRegistrationRequest(
                "NewUser", "new@example.com", "Password123!"
        );

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("encoded_pass");
        when(userRepository.save(any(EntityUsers.class))).thenAnswer(invocation -> {
            EntityUsers saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            saved.setRegistrationDate(Instant.now());
            return saved;
        });

        // ★ ДОБАВЛЕНО: Мок для Kafka (чтобы не было NullPointerException)
        when(kafkaTemplate.send(any(org.springframework.messaging.Message.class)))
                .thenReturn(java.util.concurrent.CompletableFuture.completedFuture(null));

        // WHEN
        UserResponse response = userService.registeredUser(request);

        // THEN
        assertEquals("NewUser", response.getUserName());
        assertEquals("new@example.com", response.getEmail());
        assertNotNull(response.getId());

        // Проверяем, что событие ушло в Kafka (через мок)
        verify(kafkaTemplate, times(1)).send(any(org.springframework.messaging.Message.class));
        verify(userRepository, times(1)).save(any(EntityUsers.class));
    }

    // ТЕСТ 6: Ошибка регистрации (email уже занят)
    @Test
    void registeredUser_DuplicateEmail_ThrowsException() {
        // GIVEN
        UserRegistrationRequest request = new UserRegistrationRequest(
                "NewUser", "existing@example.com", "Password123!"
        );
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // WHEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registeredUser(request);
        });

        // THEN
        assertTrue(exception.getMessage().contains("уже существует"));
        verify(userRepository, never()).save(any(EntityUsers.class));
    }
}