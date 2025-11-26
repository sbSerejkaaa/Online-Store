package com.example.order.controller.rest;

import com.example.order.controller.dto.OrderRequest;
import com.example.order.controller.dto.OrderResponse;

import com.example.order.service.processor.OrderProcessor;
import com.example.order.service.command.CreateOrderCommand;
import com.example.order.service.converter.OrderCommandConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderProcessor orderProcessor;
    private final OrderCommandConverter converter;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody @Valid OrderRequest request) {

        log.info("📦 Создание заказа для продукта: {}", request.getProductName());

        // Конвертируем DTO в команду
        CreateOrderCommand command = converter.toCreateOrderCommand(request);

        // Передаем команду в процессор
        OrderResponse response = orderProcessor.handleCommand(command);

        log.info("✅ Заказ создан: {}", response.getOrderId());
        return ResponseEntity.accepted().body(response);
    }


  /*  @GetMapping("/{orderId}/history")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderHistoryResponse> getOrderHistory(@PathVariable UUID orderId){
        return orderHistoryService.findByOrderId(orderId).stream().map(orderHistory -> {
            OrderHistoryResponse orderHistoryResponse = new OrderHistoryResponse();
            BeanUtils.copyProperties(orderHistory, orderHistoryResponse);
            return orderHistoryResponse;
        }).toList();

   */
}
