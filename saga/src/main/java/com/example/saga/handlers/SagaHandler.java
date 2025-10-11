package com.example.saga.handlers;

import com.example.core.command.ReserveProductCommand;
import com.example.core.event.OrderRegisteredEvent;
import com.example.saga.mapper.SagaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${order-request-topic}")
@RequiredArgsConstructor
public class SagaHandler {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String SAGA_COMMAND_TOPIC = "saga-product-commands-topic";

    @KafkaHandler
    public void handleEvent(@Payload OrderRegisteredEvent event){
        ReserveProductCommand command = SagaMapper.toReserveCommand(event);

        kafkaTemplate.send(SAGA_COMMAND_TOPIC, command);
    }
}
