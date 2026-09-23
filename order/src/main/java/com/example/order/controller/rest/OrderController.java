package com.example.order.controller.rest;

import com.example.core.model.Orders;
import com.example.order.controller.dto.OrderRequest;
import com.example.order.controller.dto.OrderResponse;
import com.example.order.mapper.OrderResponseMapper;
import com.example.order.service.OrderService;
import com.example.order.service.command.CreateOrderCommand;
import com.example.order.service.converter.OrderCommandConverter;
import com.example.order.service.processor.OrderProcessor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProcessor orderProcessor;
    private final OrderCommandConverter converter;
    private final OrderService orderService;
    private final OrderResponseMapper responseMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody @Valid OrderRequest request) {

        log.info("Создание заказа: {} товаров, userId={}", request.getItems().size(), userId);

        CreateOrderCommand command = converter.toCreateOrderCommand(request, userId);
        OrderResponse response = orderProcessor.handleCommand(command);

        log.info("Заказ принят: {}", response.getOrderId());
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
        log.debug("Запрос заказа: {}", orderId);

        Orders order = orderService.getOrder(orderId);
        return ResponseEntity.ok(responseMapper.toResponse(order));
    }

    /**
     * Список заказов пользователя.
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            @RequestHeader("X-User-Id") UUID userId) {
        log.debug("Запрос заказов userId={}", userId);

        List<OrderResponse> orders = orderService.getOrdersByUserId(userId).stream()
                .map(responseMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(orders);
    }

    /**
     * Количество заказов (для дашборда).
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getOrderCount(@RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(orderService.countByUserId(userId));
    }
}