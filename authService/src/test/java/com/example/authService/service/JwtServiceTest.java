package com.example.authService.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private final String SECRET = "superSecretKeyForAuthService2025MustBeLongEnoughForHS256";
    private final long ACCESS_EXPIRATION = 900000L; // 15 минут
    private final long REFRESH_EXPIRATION = 604800000L; // 7 дней

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, ACCESS_EXPIRATION, REFRESH_EXPIRATION);
    }

    @Test
    void generateAndParseAccessToken_Success() {
        // GIVEN
        String userId = "123e4567-e89b-12d3-a456-426614174000";
        String email = "test@example.com";
        String role = "USER";

        // WHEN
        String token = jwtService.generateAccessToken(userId, email, role);
        Claims claims = jwtService.parseToken(token);

        // THEN
        assertEquals(userId, claims.getSubject());
        assertEquals(email, claims.get("email", String.class));
        assertEquals(role, claims.get("role", String.class));
        assertEquals("access", claims.get("type", String.class));
        assertNotNull(claims.getExpiration());
    }

    @Test
    void generateAndParseRefreshToken_Success() {
        // GIVEN
        String userId = "123e4567-e89b-12d3-a456-426614174000";
        String email = "test@example.com";
        String role = "ADMIN";

        // WHEN
        String token = jwtService.generateRefreshToken(userId, email, role);
        Claims claims = jwtService.parseToken(token);

        // THEN
        assertEquals(userId, claims.getSubject());
        assertEquals(email, claims.get("email", String.class));
        assertEquals(role, claims.get("role", String.class));
        assertEquals("refresh", claims.get("type", String.class));
    }

    @Test
    void parseInvalidToken_ThrowsException() {
        // GIVEN
        String invalidToken = "invalid.jwt.token";

        // WHEN & THEN
        assertThrows(Exception.class, () -> {
            jwtService.parseToken(invalidToken);
        });
    }
}