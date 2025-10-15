package com.example.product.handler;

import com.example.core.command.ReserveProductCommand;
import com.example.core.event.ProductRegisteredEvent;
import com.example.core.event.ProductReservationFailedEvent;
import com.example.core.model.Product;
import com.example.core.status.ProductStatus;
import com.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Slf4j
@Component
@KafkaListener(topics = "saga-product-commands-topic") // слушаю САГУ
@RequiredArgsConstructor

public class ProductCommandHandler {

    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String INVENTORY_RESERVED_EVENTS_TOPIC = "product-reserved-events-topic";
    private static final String INVENTORY_FAILED_TOPIC = "product-failed-topic";

    @KafkaHandler
    public void handleCommand(@Payload ReserveProductCommand command){
        try{
            Product desiredProduct = new Product(command.getProductId(), command.getProductQuantity());
            Product reservedProduct = productService.reserve(desiredProduct, command.getOrderId());
            ProductRegisteredEvent event = ProductRegisteredEvent.builder()
                    .inventoryId(reservedProduct.getProductId())
                    .orderId(command.getOrderId())
                    .productName(reservedProduct.getProductName())
                    .quantity(reservedProduct.getQuantity())  // зарезервированное количество
                    .price(reservedProduct.getPrice())
                    .status(ProductStatus.RESERVED)
                    .createdAt(Instant.now())
                    .build();

            kafkaTemplate.send(INVENTORY_RESERVED_EVENTS_TOPIC, event);

        } catch ( Exception e){
            log.error(e.getLocalizedMessage(), e);
            ProductReservationFailedEvent productReservationFailedEvent = new ProductReservationFailedEvent(command.getProductId(),
                    command.getOrderId(), command.getProductQuantity());
            kafkaTemplate.send(INVENTORY_FAILED_TOPIC, productReservationFailedEvent);

        }


    }


}
