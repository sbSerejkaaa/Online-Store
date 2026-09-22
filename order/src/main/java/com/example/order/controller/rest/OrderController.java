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

import java.util.UUID;

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
            @RequestBody @Valid OrderRequest request) {

        log.info("Создание заказа: {} товаров для accountId={}",
                request.getItems().size(), request.getAccountId());

        CreateOrderCommand command = converter.toCreateOrderCommand(request);
        OrderResponse response = orderProcessor.handleCommand(command);

        log.info("Заказ принят: {}", response.getOrderId());
        return ResponseEntity.accepted().body(response);
    }

    /**
     * Получить статус заказа (для polling)
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
        log.debug("Запрос заказа: {}", orderId);

        Orders order = orderService.getOrder(orderId);
        OrderResponse response = responseMapper.toResponse(order);

        return ResponseEntity.ok(response);
    }
}