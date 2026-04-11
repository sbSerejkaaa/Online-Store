package com.example.order.service.handler;

import com.example.core.event.order.OrderCreatedEvent;
import com.example.core.model.Orders;
import com.example.core.status.OrderStatus;
import com.example.order.kafka.producer.ProducerOrder;
import com.example.order.mapper.OrderEntityMapper;
import com.example.order.entity.OrderEntity;
import com.example.order.mapper.OrderEventMapper;
import com.example.order.repository.OrderRepository;
import com.example.order.service.command.CreateOrderCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateOrderHandler {
    private final OrderRepository orderRepository;
    private final OrderEntityMapper entityMapper;
    private final OrderEventMapper orderEventMapper;
    private final ProducerOrder eventPublisher;

    @Transactional
    public Orders handle(CreateOrderCommand command) {
        log.info("Создание заказа для продукта: {}", command.getProductName());

        // command УЖЕ содержит:
        // - productName (валидированный)
        // - quantity (валидированный)
        // - commandId (сгенерированный)
        // - timestamp (добавленный)

        // 1. СОЗДАЕМ OrderEntity БЕЗ СУММЫ
        OrderEntity entity = OrderEntity.builder()
                .productName(command.getProductName())
                .quantity(command.getQuantity())
                .status(OrderStatus.IN_PROCESS)
                .build();



        // totalAmount = null (сумму рассчитает ProductService)

        // 2. СОХРАНЯЕМ в БД
        OrderEntity savedEntity = orderRepository.save(entity);

        // 3. Маппим в Доменную модель
        Orders order = entityMapper.toDomain(savedEntity);

        // 4. Создаем ивент БЕЗ СУММЫ
        OrderCreatedEvent event = orderEventMapper.toEvent(order);
        event.setAccountId(command.getAccountId());

        // 5. Отправляем в Kafka для Саги
        eventPublisher.publishOrderCreated(event);

        log.info("OrderCreatedEvent отправлен в сагу: {}", order.getId());
        return order;
    }
}
