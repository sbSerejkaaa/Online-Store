package com.example.cart.controller;

import com.example.cart.dto.AddItemRequest;
import com.example.cart.dto.UpdateQuantityRequest;
import com.example.cart.model.Cart;
import com.example.cart.model.CartItem;
import com.example.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * Получить корзину текущего пользователя.
     */
    @GetMapping
    public ResponseEntity<Cart> getCart(@RequestHeader("X-User-Id") UUID userId) {
        log.info("Запрос корзины для userId={}", userId);
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    /**
     * Добавить товар в корзину.
     */
    @PostMapping("/items")
    public ResponseEntity<Cart> addItem(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody @Valid AddItemRequest request) {

        log.info("Добавление товара в корзину userId={}, productId={}", userId, request.getProductId());

        CartItem item = CartItem.builder()
                .productId(request.getProductId())
                .productName(request.getProductName())
                .imageUrl(request.getImageUrl())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .build();

        return ResponseEntity.ok(cartService.addItem(userId, item));
    }

    /**
     * Изменить количество товара.
     */
    @PutMapping("/items/{productId}")
    public ResponseEntity<Cart> updateQuantity(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID productId,
            @RequestBody @Valid UpdateQuantityRequest request) {

        log.info("Изменение количества userId={}, productId={}, quantity={}",
                userId, productId, request.getQuantity());

        return ResponseEntity.ok(cartService.updateQuantity(userId, productId, request.getQuantity()));
    }

    /**
     * Удалить товар из корзины.
     */
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Cart> removeItem(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID productId) {

        log.info("Удаление товара userId={}, productId={}", userId, productId);
        return ResponseEntity.ok(cartService.removeItem(userId, productId));
    }

    /**
     * Очистить корзину.
     */
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") UUID userId) {
        log.info("Очистка корзины userId={}", userId);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}