package com.example.order.service;

import com.example.core.event.OrderRegisteredEvent;
import com.example.core.model.Orders;
import com.example.order.entity.OrderEntity;
import com.example.order.mapper.OrderMapper;
import com.example.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String ORDER_REGISTERED_TOPIC = "order-request-topic";

    @Override
    public Orders createOrder(Orders order) {
        OrderEntity entity = orderMapper.toEntity(order);           // Core → Entity

        OrderEntity savedEntity = orderRepository.save(entity);     // Сохраняем в БД

        // Отправляем событие в Kafka
        OrderRegisteredEvent event = orderMapper.toEvent(savedEntity);

        kafkaTemplate.send(ORDER_REGISTERED_TOPIC,entity.getOrderId().toString(), event);

        // Возвращаем результат
        return orderMapper.toCoreModel(savedEntity);               // Entity → Core
    }


}
