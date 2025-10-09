package com.example.order.controller;

import com.example.core.model.Orders;
import com.example.order.dto.OrderRegistrationRequest;
import com.example.order.dto.OrderResponse;

import com.example.order.mapper.OrderMapper;
import com.example.order.service.OrderServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;
    private final OrderMapper orderMapper;
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderResponse placeOrder(@RequestBody @Valid OrderRegistrationRequest request){
        // 1. DTO → Core Model через маппер
        Orders order = orderMapper.toCoreModel(request);

        // 2. Вызываем сервис с Core Model
        Orders createdOrder = orderService.createOrder(order);

        // 3. Core Model → DTO через маппер
        return orderMapper.toResponse(createdOrder);
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
