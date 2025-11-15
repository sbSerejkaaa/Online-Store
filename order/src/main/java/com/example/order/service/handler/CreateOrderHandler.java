package com.example.order.service.handler;

import com.example.core.event.order.OrderCreatedEvent;
import com.example.core.model.Orders;
import com.example.order.kafka.producer.OrderEventPublisher;
import com.example.order.mapper.OrderEntityMapper;
import com.example.order.entity.OrderEntity;
import com.example.order.mapper.OrderEventMapper;
import com.example.order.repository.OrderRepository;
import com.example.order.service.command.CreateOrderCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateOrderHandler {
    private final OrderRepository orderRepository;
    private final OrderEntityMapper entityMapper;
    private final OrderEventMapper orderEventMapper;
    private final OrderEventPublisher eventPublisher;

    @Transactional
    public Orders handle(CreateOrderCommand command) {
        log.info("🏭 Создание заказа для продукта: {}", command.getProductName());

        // 1. ЗАПРАШИВАЕМ ЦЕНУ У PRODUCT SERVICE
        BigDecimal productPrice = productPriceClient.getPrice(command.getProductName());
        BigDecimal totalAmount = productPrice.multiply(new BigDecimal(command.getQuantity()));

         // 1. СОЗДАЕМ Entity
         // 2. command.getProductName(), command.getQuantity() - из команды(сырые данные от пользователя)
         // 3. Время создания заказа и статус из конструктора сущности
         // 4. ID будет создано на стороне JPA в методе save()
         // 5. Нужно в будущем подтянуть ID зарегистрированного пользователя !!!

        OrderEntity entity = new OrderEntity(
                command.getProductName(),
                command.getQuantity());

        // 2. СОХРАНЯЕМ в БД
        OrderEntity savedEntity = orderRepository.save(entity);

        // 3. Маппим в Доменную модель из данныx БД
        Orders order = entityMapper.toDomain(savedEntity);     // Entity → Domain

        // 4. Создаем ивент на основе модели
        OrderCreatedEvent event = orderEventMapper.toEvent(order); // Domain → Event

        // 5. Отправляем данные в ПРОДЮСЕР
        eventPublisher.publishOrderCreated(event);
        log.info("📢 Событие OrderCreated опубликовано для заказа: {}", order.getId());

        return order;
    }
}
