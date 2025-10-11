package com.example.order.service;

import com.example.core.event.OrderRegisteredEvent;
import com.example.core.model.Orders;
import com.example.order.entity.OrderEntity;
import com.example.order.mapper.OrderMapper;
import com.example.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.AuthenticationException;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.errors.TopicAuthorizationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

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

        try {
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(ORDER_REGISTERED_TOPIC, savedEntity.getId().toString(), event);
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    if (ex instanceof TimeoutException) {
                        log.error("Таймаут соединения с Kafka");
                    } else if (ex instanceof AuthenticationException) {
                        log.error("Ошибка аутентификации в Kafka");
                    } else if (ex instanceof TopicAuthorizationException) {
                        log.error("Нет прав на запись в топик");
                    } else {
                        log.error("Другая ошибка: {}", ex.getMessage());
                    }
                } else {
                    log.info("Событие отправлено в топик: {}", ORDER_REGISTERED_TOPIC);
                    log.info("Inventory ID {}", savedEntity.getId());
                }
            });

        } catch (Exception e) {
            log.error("Ошибка при отправке события для продукта {}: {}",
                    order.getProductName(), e.getMessage());
        }
        log.info("Событие отправлено в кафку");

        // Возвращаем результат
        return orderMapper.toCoreModel(savedEntity);               // Entity → Core
    }


}
