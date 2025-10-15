package com.example.product.service;

import com.example.core.event.OrderRegisteredEvent;
import com.example.core.event.ProductRegisteredEvent;

import com.example.core.exception.ProductInsufficientQuantityException;
import com.example.core.model.Product;
import com.example.core.status.ProductStatus;
import com.example.product.dto.ProductRegistrationRequest;
import com.example.product.dto.ProductResponse;

import com.example.product.entity.EntityProduct;
import com.example.product.mapper.ProductMapper;
import com.example.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.AuthenticationException;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.errors.TopicAuthorizationException;
import org.springframework.beans.BeanUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProductMapper productMapper;
    private static final String INVENTORY_REGISTERED_TOPIC = "inventory-reserved-topic";

    // Этим занимаются АДМИНЫ - настроить роли в будущем
    @Override
    public Product createProduct(Product product) {
        log.info("Создание заказа");
        EntityProduct entityProduct = productMapper.toEntity(product);

        EntityProduct saveEntityProduct = productRepository.save(entityProduct);
        log.info("Товар внесен в БД");

        ProductRegisteredEvent productEvent = productMapper.toEvent(saveEntityProduct);
        try {

            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(INVENTORY_REGISTERED_TOPIC, saveEntityProduct.getId().toString(), productEvent);
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
                    log.info("Inventory ID {}", saveEntityProduct.getId());
                }
            });

        } catch (Exception e) {
            log.error("Ошибка при отправке события для продукта {}: {}",
                    product.getProductName(), e.getMessage());
        }
        log.info("Событие отправлено в кафку");

        return productMapper.toModel(saveEntityProduct);
    }

    @Override
    public Product reserve(Product desiredProduct, UUID orderId) {
        EntityProduct productEntity = productRepository.findById(desiredProduct.getProductId()).orElseThrow();
        if (desiredProduct.getQuantity() > productEntity.getQuantity()) {
            throw new ProductInsufficientQuantityException(productEntity.getId(), orderId);
        }

        productEntity.setQuantity(productEntity.getQuantity() - desiredProduct.getQuantity());

        productRepository.save(productEntity);

        var reservedProduct = new Product();
        BeanUtils.copyProperties(productEntity, reservedProduct);
        reservedProduct.setQuantity(desiredProduct.getQuantity());
        return reservedProduct;
    }

    @Override
    public void cancelReservation(Product productToCancel, UUID orderId) {

        EntityProduct productEntity = productRepository.findById(productToCancel.getProductId()).orElseThrow();
        productEntity.setQuantity(productEntity.getQuantity() + productToCancel.getQuantity());
        productRepository.save(productEntity);

    }

    @Override
    public List<Product> findAll() {
         return productRepository.findAll().stream()
                .map(entity -> new Product(entity.getId(), entity.getProductName(), entity.getQuantity(), entity.getPrice()))
                .collect(Collectors.toList());
    }

}

