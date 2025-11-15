package com.example.authService.config;

import com.example.core.event.users.UserRegisteredEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AuthKafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public NewTopic userRegisteredTopic(){
        return TopicBuilder.name("user-registered-topic")
                .partitions(3)
                .replicas(3)
                .configs(Map.of("min.insync.replicas", "2"))
                .build();
    }

    @Bean
    public ProducerFactory<String, UserRegisteredEvent> producerFactory(){
        Map<String, Object> configProducer = new HashMap<>();
        //Основа
        configProducer.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProducer.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProducer.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        //Надежность
        configProducer.put(ProducerConfig.ACKS_CONFIG, "all");
        configProducer.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProducer.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        //Таймауты
        configProducer.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 60000);
        configProducer.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000);
        configProducer.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 500);
        //Батч-сайзинг
        configProducer.put(ProducerConfig.LINGER_MS_CONFIG, 100);
        configProducer.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        configProducer.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");


        return new DefaultKafkaProducerFactory<>(configProducer);
    }

    @Bean
    public KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }
}
