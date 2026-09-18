package com.example.cart.service;

import com.example.cart.model.Cart;
import com.example.cart.model.CartItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final RedisTemplate<String, Cart> cartRedisTemplate;
    private static final String CART_KEY_PREFIX = "cart:";
    private static final Duration CART_TTL = Duration.ofDays(30);

    /**
     * Получить корзину пользователя.
     * Если нет — вернуть пустую.
     */
    public Cart getCart(UUID userId) {
        String key = CART_KEY_PREFIX + userId;
        Cart cart = cartRedisTemplate.opsForValue().get(key);

        if (cart == null) {
            log.info("Корзина не найдена для userId={}, создаём пустую", userId);
            cart = Cart.builder()
                    .userId(userId)
                    .items(new ArrayList<>())
                    .updatedAt(Instant.now())
                    .build();
        }

        return cart;
    }

    /**
     * Добавить товар в корзину.
     * Если товар уже есть — увеличить количество.
     */
    public Cart addItem(UUID userId, CartItem item) {
        Cart cart = getCart(userId);

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(item.getProductId()))
                .findFirst();

        if (existing.isPresent()) {
            CartItem existingItem = existing.get();
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
            log.info("Увеличено количество товара {} до {}", item.getProductId(), existingItem.getQuantity());
        } else {
            cart.getItems().add(item);
            log.info("Добавлен товар {} в корзину пользователя {}", item.getProductId(), userId);
        }

        cart.recalculate();
        saveCart(cart);
        return cart;
    }

    /**
     * Изменить количество конкретного товара.
     */
    public Cart updateQuantity(UUID userId, UUID productId, Integer quantity) {
        Cart cart = getCart(userId);

        cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    log.info("Количество товара {} изменено на {}", productId, quantity);
                });

        // Если quantity = 0 — удалить товар
        cart.getItems().removeIf(i -> i.getQuantity() <= 0);

        cart.recalculate();
        saveCart(cart);
        return cart;
    }

    /**
     * Удалить товар из корзины.
     */
    public Cart removeItem(UUID userId, UUID productId) {
        Cart cart = getCart(userId);
        cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        cart.recalculate();
        saveCart(cart);
        log.info("Товар {} удалён из корзины {}", productId, userId);
        return cart;
    }

    /**
     * Очистить корзину полностью.
     */
    public void clearCart(UUID userId) {
        String key = CART_KEY_PREFIX + userId;
        cartRedisTemplate.delete(key);
        log.info("Корзина пользователя {} очищена", userId);
    }

    /**
     * Сохранить корзину в Redis с TTL.
     */
    private void saveCart(Cart cart) {
        String key = CART_KEY_PREFIX + cart.getUserId();
        cartRedisTemplate.opsForValue().set(key, cart, CART_TTL);
    }
}