package com.example.payment.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
public class KafkaTopicConfig {
    private static final int PARTITIONS = 3;
    private static final int REPLICAS = 1;

    @Bean
    public NewTopic paymentRequestTopic() {
        return buildTopic("payment.event.topic");
    }

    @Bean
    public NewTopic paymentFailedTopic() {
        return buildTopic("payment.failed.topic");
    }

    @Bean
    public NewTopic paymentRequestDltTopic() {
        return buildDltTopic("payment.request.topic.DLT");
    }

    private NewTopic buildTopic(String name) {
        return TopicBuilder.name(name)
                .partitions(PARTITIONS)
                .replicas(REPLICAS)
                .configs(Map.of("min.insync.replicas", "1"))
                .build();
    }

    private NewTopic buildDltTopic(String name) {
        return TopicBuilder.name(name)
                .partitions(PARTITIONS)
                .replicas(REPLICAS)
                .configs(Map.of(
                        "retention.ms", "1209600000",
                        "min.insync.replicas", "1"
                ))
                .build();
    }

}
