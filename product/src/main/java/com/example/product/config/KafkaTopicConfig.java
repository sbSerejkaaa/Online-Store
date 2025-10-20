package com.example.product.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;
@Configuration
public class KafkaTopicConfig {
    private static final int PARTITIONS = 3;
    private static final int REPLICAS = 3;

    @Bean
    public NewTopic productRequestTopic() {
        return buildTopic("product.request.topic");
    }

    @Bean
    public NewTopic productFailedTopic() {
        return buildTopic("product.failed.topic");
    }

    @Bean
    public NewTopic productRequestDltTopic() {
        return buildDltTopic("product.request.topic.DLT");
    }

    private NewTopic buildTopic(String name) {
        return TopicBuilder.name(name)
                .partitions(PARTITIONS)
                .replicas(REPLICAS)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }

    private NewTopic buildDltTopic(String nameDlt) {
        return TopicBuilder.name(nameDlt)
                .partitions(PARTITIONS)
                .replicas(REPLICAS)
                .configs(Map.of(
                        "retention.ms", "1209600000",
                        "min.insync.replicas", "2"
                ))
                .build();
    }
}
