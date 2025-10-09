package com.example.product.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "saga-commands-topic")
public class ProductCommandHandler {

    @KafkaHandler
    public void handleCommand(@Payload ){

    }
}
