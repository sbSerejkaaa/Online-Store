package com.example.order.service;

import com.example.core.model.Orders;
import com.example.core.status.OrderStatus;
import com.example.order.mapper.OrderEntityMapper;
import com.example.order.entity.OrderEntity;
import com.example.order.repository.OrderRepository;
import com.example.order.service.command.CancelOrderCommand;
import com.example.order.service.command.ConfirmOrderCommand;
import com.example.order.service.command.CreateOrderCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderEntityMapper entityMapper;

    @Override
    @Transactional
    public Orders createOrder(CreateOrderCommand command) {
        // Теперь это делает CreateOrderHandler!
        throw new UnsupportedOperationException("Use CreateOrderHandler instead");
    }

    @Override
    @Transactional
    public void confirmOrder(UUID orderId) {
        OrderEntity order = null;
        try {
            order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new Exception("Order not found: " + orderId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        log.info("Order {} confirmed successfully", orderId);
    }

    @Override
    @Transactional
    public void cancelOrder(UUID orderId) {  // ← UUID + причина
        OrderEntity order = null;
        try {
            order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new Exception("Отмена заказа Id: " + orderId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        log.info("Заказ отменен: {}", orderId);
    }
}
