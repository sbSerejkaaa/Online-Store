package com.example.order.service;

import com.example.order.dto.OrderRegistrationRequest;
import com.example.order.dto.OrderResponse;
import com.example.order.entity.Orders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse createdOrder(OrderRegistrationRequest request){
        log.info("Создаем заказ {} ", request.getOrderName());
        Orders orders = new Orders();// Скорей всего нужно использовать Лист,
        // так как нам нужен список выбранных товаров клиента

    }

    private OrderResponse responseToFrontend(){

    }
}
