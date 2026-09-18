package com.example.authService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;
    private static final long REFRESH_TTL_DAYS = 7;

    public void save(String userId, String refreshToken) {
        redisTemplate.opsForValue().set(
                "refresh:" + userId,
                refreshToken,
                REFRESH_TTL_DAYS,
                TimeUnit.DAYS
        );
    }

    public String get(String userId) {
        return redisTemplate.opsForValue().get("refresh:" + userId);
    }

    public void delete(String userId) {
        redisTemplate.delete("refresh:" + userId);
    }

    public boolean isValid(String userId, String token) {
        String stored = get(userId);
        return stored != null && stored.equals(token);
    }
}
