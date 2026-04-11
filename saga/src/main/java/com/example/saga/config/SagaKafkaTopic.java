package com.example.saga.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
public class SagaKafkaTopic {

    @Bean
    public NewTopic sagaOrderCommandsTopic() {
        return buildCommandTopic("saga.orders.commands");
    }

    @Bean
    public NewTopic sagaProductCommandsTopic() {
        return buildCommandTopic("saga.products.commands");
    }

    @Bean
    public NewTopic sagaPaymentCommandsTopic() {
        return buildCommandTopic("saga.payments.commands");
    }


    @Bean
    public NewTopic sagaOrderCommandsDltTopic() {
        return buildDltTopic("saga.orders.commands.DLT");
    }

    @Bean
    public NewTopic sagaPaymentCommandsDltTopic() {
        return buildDltTopic("saga.payments.commands.DLT");
    }

    @Bean
    public NewTopic sagaProductCommandsDltTopic() {
        return buildDltTopic("saga.products.commands.DLT");
    }

    private NewTopic buildCommandTopic(String name) {
        return TopicBuilder.name(name)
                .partitions(3).replicas(1)
                .configs(Map.of("min.insync.replicas", "1", "retention.ms", "604800000")) // 7 дней
                .build();
    }

    private NewTopic buildDltTopic(String name) {
        return TopicBuilder.name(name)
                .partitions(3).replicas(1)
                .configs(Map.of("retention.ms", "1209600000")) // 14 дней для DLT
                .build();
    }

}

