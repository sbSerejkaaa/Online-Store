package com.example.order.saga;

import com.example.core.command.ReserveInventoryCommand;
import com.example.core.event.OrderRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${order-request-topic}")
@RequiredArgsConstructor
public class OrderSaga {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String SAGA_COMMAND_TOPIC = "saga-commands-topic";

    @KafkaHandler
    public void handleEvent(@Payload OrderRegisteredEvent event){
        ReserveInventoryCommand command = new ReserveInventoryCommand(
                event.getInventoryId(),
                event.getProductQuantity(),
                event.getOrderId()
        );

        kafkaTemplate.send(SAGA_COMMAND_TOPIC, command);
    }



}
