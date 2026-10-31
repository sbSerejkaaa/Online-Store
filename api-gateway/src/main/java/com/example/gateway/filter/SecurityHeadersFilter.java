package com.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getResponse().getHeaders().add(
                "Content-Security-Policy",
                "default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                        "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://fonts.gstatic.com; " +
                        "img-src 'self' data:; " +
                        "font-src 'self' https://fonts.gstatic.com; " +
                        "connect-src 'self'"
        );
        exchange.getResponse().getHeaders().add("X-Content-Type-Options", "nosniff");
        exchange.getResponse().getHeaders().add("X-Frame-Options", "DENY");
        exchange.getResponse().getHeaders().add("X-XSS-Protection", "1; mode=block");
        exchange.getResponse().getHeaders().add("Referrer-Policy", "strict-origin-when-cross-origin");

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}