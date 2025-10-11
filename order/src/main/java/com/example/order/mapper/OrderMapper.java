package com.example.order.mapper;

import com.example.core.event.OrderRegisteredEvent;
import com.example.core.status.OrderStatus;
import com.example.core.model.Orders;
import com.example.order.dto.OrderRegistrationRequest;
import com.example.order.dto.OrderResponse;
import com.example.order.entity.OrderEntity;
import lombok.Builder;

import java.time.Instant;
@Builder
public class OrderMapper {

    // DTO → Core Model (для контроллера) Преобразует данные от клиента (DTO) в общую модель для бизнес-логики
    public Orders toCoreModel(OrderRegistrationRequest request) {
        Orders order = new Orders();
        //    order.setUserId(request.getUserId());           // Берем ID пользователя из запроса
        order.setProductName(request.getProductName()); // Берем название товара из запроса
        order.setTotalAmount(request.getCount());       // Берем количество товара из запроса
        order.setStatus(OrderStatus.PENDING); // Устанавливаем начальный статус "В обработке"
        return order;
    }

    // Core Model → Entity (для сервиса) Преобразует бизнес-модель в JPA сущность для сохранения в БД
    public OrderEntity toEntity(Orders order) {
        OrderEntity entity = new OrderEntity();
        // entity.setUserId(order.getUserId());            // Переносим ID пользователя
        entity.setProductName(order.getProductName());  // Переносим название товара
        entity.setTotalAmount(order.getTotalAmount());  // Переносим количество
        entity.setStatus(order.getStatus());            // Переносим статус
        entity.setCreatedAt(Instant.now());             // устанавливаем текущее время
        return entity;
    }

    // Entity → Core Model (для сервиса) Преобразует данные из БД обратно в бизнес-модель
    public Orders toCoreModel(OrderEntity entity) {
        Orders order = new Orders();
        order.setId(entity.getOrderId());                    // Берем ID сгенерированный БД
 //       order.setUserId(entity.getUserId());            // Переносим ID пользователя
        order.setProductName(entity.getProductName());  // Переносим название товара
        order.setTotalAmount(entity.getTotalAmount());  // Переносим количество
        order.setStatus(entity.getStatus());            // Переносим статус
        return order;
    }

    // Core Model → DTO (для контроллера) Преобразует бизнес-модель в DTO для ответа клиенту
    public OrderResponse toResponse(Orders order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());                    // ID заказа для клиента
        response.setProductName(order.getProductName());  // Название товара для клиента
        response.setProductCount(order.getTotalAmount()); // totalAmount → productCount (переименовываем)
        response.setStatus(order.getStatus());            // Статус заказа для клиента
        return response;
    }

    // Entity → Event (для Kafka) Преобразует данные из БД в событие для отправки в Kafka
    public OrderRegisteredEvent toEvent(OrderEntity entity) {
        OrderRegisteredEvent event = new OrderRegisteredEvent();
        event.setOrderId(entity.getOrderId());                   // ID заказа для события
  //      event.setUserId(entity.getUserId());           // ID пользователя для события
        event.setProductName(entity.getProductName()); // Название товара для события
        event.setProductQuantity(entity.getTotalAmount()); // Количество для события
        event.setStatus(entity.getStatus());           // Статус для события
        event.setCreatedAt(entity.getCreatedAt());     // Время создания для события
        return event;
    }
}
