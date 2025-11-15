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
    public void confirmOrder(ConfirmOrderCommand command) {
        OrderEntity order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + command.getOrderId()));

        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        log.info("✅ Заказ подтвержден: {}", command.getOrderId());
    }

    @Override
    @Transactional
    public void cancelOrder(CancelOrderCommand command) {
        OrderEntity order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + command.getOrderId()));

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        log.info("❌ Заказ отменен: {}, причина: {}", command.getOrderId(), command.getReason());
    }
}
