package com.example.order.service;

import com.example.core.model.Orders;
import com.example.core.status.OrderStatus;
import com.example.order.entity.OrderEntity;
import com.example.order.mapper.OrderEntityMapper;
import com.example.order.repository.OrderRepository;
import com.example.order.service.command.CreateOrderCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEntityMapper entityMapper;

    @Override
    @Transactional
    public Orders createOrder(CreateOrderCommand command) {
        throw new UnsupportedOperationException("Use CreateOrderHandler instead");
    }

    @Override
    @Transactional
    public void confirmOrder(UUID orderId, BigDecimal totalAmount) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
        log.info("Order {} confirmed with amount {}", orderId, totalAmount);
    }

    @Override
    @Transactional
    public void cancelOrder(UUID orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.info("Order cancelled: {}", orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public Orders getOrder(UUID orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        return entityMapper.toDomain(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Orders> getOrdersByUserId(UUID userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(entityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countByUserId(UUID userId) {
        return orderRepository.countByUserId(userId);
    }
}