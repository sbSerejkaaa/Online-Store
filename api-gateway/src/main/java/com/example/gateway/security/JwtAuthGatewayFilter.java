package com.example.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JwtAuthGatewayFilter extends AbstractGatewayFilterFactory<JwtAuthGatewayFilter.Config> {

    private final JwtService jwtService;

    public JwtAuthGatewayFilter(JwtService jwtService) {
        super(Config.class);
        this.jwtService = jwtService;
    }
    @Override
    public String name() {
        return "JwtAuth";
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token = extractTokenFromCookies(exchange);

            if (token == null) {
                log.warn("JWT отсутствует. Путь: {}", exchange.getRequest().getPath());
                return unauthorized(exchange, "Требуется аутентификация");
            }

            try {
                Claims claims = jwtService.parseToken(token);

                String userId = claims.getSubject();
                String email = claims.get("email", String.class);
                String role = claims.get("role", String.class);

                if (role == null) role = "USER";

                // Прокидываем данные в downstream-сервисы через заголовки
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Email", email != null ? email : "")
                        .header("X-User-Role", role)
                        .build();

                log.debug("Аутентифицирован: userId={}, role={}", userId, role);

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (JwtException e) {
                log.warn("Невалидный JWT: {}", e.getMessage());
                return unauthorized(exchange, "Невалидный или истёкший токен");
            }
        };
    }

    private String extractTokenFromCookies(ServerWebExchange exchange) {
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("access_token");
        return cookie != null ? cookie.getValue() : null;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");

        String body = "{\"error\":\"" + message + "\"}";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    public static class Config {
        // Пока пусто — параметры не нужны
    }
}
