package com.example.product.handler;

import com.example.core.command.ReserveProductCommand;
import com.example.core.model.Product;
import com.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.management.ObjectName;
@Slf4j
@Component
@KafkaListener(topics = "saga-product-commands-topic")
@RequiredArgsConstructor
public class ProductCommandHandler {

    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaHandler
    public void handleCommand(@Payload ReserveProductCommand command){
        try{
            Product desiredProduct = new Product(command.getProductId(), command.getProductQuantity());
        } catch (){

        }


    }


}
