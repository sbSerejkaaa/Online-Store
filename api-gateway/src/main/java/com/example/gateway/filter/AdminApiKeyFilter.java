package com.example.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class AdminApiKeyFilter implements GlobalFilter, Ordered {

    @Value("${admin.api-key}")
    private String validApiKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // ★ Только для admin endpoints
        if (path.startsWith("/api/inv") || path.startsWith("/admin")) {
            String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-Key");

            if (!validApiKey.equals(apiKey)) {
                log.warn("Admin API Key missing or invalid for path: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                exchange.getResponse().getHeaders().add("Content-Type", "application/json");

                String body = "{\"error\":\"Invalid or missing API Key\"}";
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

                return exchange.getResponse().writeWith(
                        Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
                );
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -75;  // ★ после RateLimiter(-1) и CorrelationId(-100), до JwtAuth
    }
}