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
        log.info("Создание заказа с {} товарами для userId={}",
                command.getItems().size(), command.getUserId());

        OrderEntity entity = OrderEntity.builder()
                .userId(command.getUserId())
                .status(OrderStatus.IN_PROCESS)
                .build();

        for (OrderItemDto itemDto : command.getItems()) {
            OrderItemEntity itemEntity = OrderItemEntity.builder()
                    .productId(itemDto.getProductId())
                    .productName(itemDto.getProductName())
                    .quantity(itemDto.getQuantity())
                    .unitPrice(itemDto.getUnitPrice())
                    .build();
            entity.addItem(itemEntity);
        }

        OrderEntity savedEntity = orderRepository.save(entity);

        Orders order = entityMapper.toDomain(savedEntity);

        OrderCreatedEvent event = orderEventMapper.toEvent(order);

        outboxService.saveOrderCreated(event);

        log.info("Order создан и событие в outbox: orderId={}", order.getId());
        return order;
    }
}