package com.example.product.service;

import com.example.core.event.InventoryRegisteredEvent;
import com.example.core.status.InventoryStatus;
import com.example.product.dto.InventoryRegistrationRequest;
import com.example.product.dto.InventoryResponse;
import com.example.product.entity.Inventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.AuthenticationException;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.errors.TopicAuthorizationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String INVENTORY_REGISTERED_TOPIC = "inventory-reserved-topic";

    // Этим занимаются АДМИНЫ - настроить роли в будущем
    @Transactional
    public InventoryResponse registeredInventory(InventoryRegistrationRequest request){
        log.info("Добавление товара на сайт");
        Inventory inventory = new Inventory(
                request.getNameInventory(),
                request.getQuantity(),
                request.getPrice()
        );

        if(inventory.getQuantity() == null){
            inventory.setStatus(InventoryStatus.OUT_OF_STOCK);
            log.info("Товара нет на складе!");
            throw new NullPointerException();
        }

        inventoryRepository.save(inventory);
        log.info("Товар внесен в БД");

        sendUserRegisteredEvent(inventory);
        log.info("Событие отправлено в кафку");

        return responseToFrontend(inventory);

    }
    private void sendUserRegisteredEvent(Inventory inventory) {
        try {
            InventoryRegisteredEvent event = new InventoryRegisteredEvent(inventory.getId(), inventory.getQuantity(),
                    inventory.getPrice(), inventory.getStatus(), inventory.getCreatedAt()
            );

            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(INVENTORY_REGISTERED_TOPIC, inventory.getId().toString(), event);
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
                    log.info("Событие отправлено в топик: {}", INVENTORY_REGISTERED_TOPIC);
                    log.info("Inventory ID {}", inventory.getId());
                }
            });

        } catch (Exception e) {
            log.error("Ошибка при отправке события для продукта {}: {}",
                    inventory.getProductName(), e.getMessage());
        }
    }


    private InventoryResponse responseToFrontend(Inventory inventory){

        InventoryResponse response = new InventoryResponse();
        response.setId(inventory.getId());
        response.setNameInventory(inventory.getProductName());
        response.setPrice(inventory.getPrice());
        response.setQuantity(inventory.getQuantity());

        return response;
    }


}
