package com.example.saga.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
public class SagaKafkaTopic {

    @Bean
    public NewTopic sagaCommandsTopic() {
        return TopicBuilder.name("saga-product-commands-topic")
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }

    @Bean
    public NewTopic sagaEventsTopic() {
        return TopicBuilder.name("saga-events-topic")
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }

    @Bean
    public NewTopic sagaCommandsDltTopic() {
        return TopicBuilder.name("saga-product-commands-topic.DLT")
                .partitions(3)
                .replicas(3)
                .configs(Map.of(
                        "retention.ms", "1209600000",
                        "min.insync.replicas", "2"
                ))
                .build();
    }

    @Bean
    public NewTopic sagaEventsDltTopic() {  // ← ДОБАВЬ ДЛЯ СОБЫТИЙ
        return TopicBuilder.name("saga-events-topic.DLT")
                .partitions(3)
                .replicas(3)
                .configs(Map.of("retention.ms", "1209600000"))
                .build();
    }
}
