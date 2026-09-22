package com.example.order.service.handler;

import com.example.core.event.order.OrderCreatedEvent;
import com.example.core.event.order.dto.OrderItemDto;
import com.example.core.model.Orders;
import com.example.core.status.OrderStatus;
import com.example.order.entity.OrderEntity;
import com.example.order.entity.OrderItemEntity;
import com.example.order.mapper.OrderEntityMapper;
import com.example.order.mapper.OrderEventMapper;
import com.example.order.repository.OrderRepository;
import com.example.order.service.command.CreateOrderCommand;
import com.example.order.service.outbox.OrderOutboxService;
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
    private final OrderOutboxService outboxService;

    @Transactional
    public Orders handle(CreateOrderCommand command) {
        log.info("Создание заказа с {} товарами для accountId={}",
                command.getItems().size(), command.getAccountId());

        // 1. СОЗДАЕМ OrderEntity
        OrderEntity entity = OrderEntity.builder()
                .status(OrderStatus.IN_PROCESS)
                .build();

        // 2. ДОБАВЛЯЕМ items
        for (OrderItemDto itemDto : command.getItems()) {
            OrderItemEntity itemEntity = OrderItemEntity.builder()
                    .productId(itemDto.getProductId())
                    .productName(itemDto.getProductName())
                    .quantity(itemDto.getQuantity())
                    .unitPrice(itemDto.getUnitPrice())
                    .build();
            entity.addItem(itemEntity);
        }

        // 3. СОХРАНЯЕМ (каскадом сохранятся items)
        OrderEntity savedEntity = orderRepository.save(entity);

        // 4. МАППИМ в доменную модель
        Orders order = entityMapper.toDomain(savedEntity);

        // 5. СОЗДАЕМ event
        OrderCreatedEvent event = orderEventMapper.toEvent(order);
        event.setAccountId(command.getAccountId());

        // 6. ❗ КЛАДЕМ в outbox (в той же транзакции)
        outboxService.saveOrderCreated(event);

        log.info("Order создан и событие в outbox: orderId={}", order.getId());
        return order;
    }
}