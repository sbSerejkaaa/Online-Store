package com.example.product.config;

import com.example.core.exception.NonRetryableException;
import com.example.core.exception.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.RetryListener;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@Configuration
public class ProductKafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;


    @Bean
    public ProducerFactory<String, Object> producerFactory() {
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
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.core.*");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "product-service-group");

        return new DefaultKafkaConsumerFactory<>(config);

    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, Object> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> {
                    // Явно указываем правильное имя DLT топика
                    return new TopicPartition("product.request.topic.DLT", record.partition());
                }
        );
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            DeadLetterPublishingRecoverer deadLetterPublishingRecoverer){


        DefaultErrorHandler errorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer,
                new FixedBackOff(3000, 3));

        errorHandler.addNotRetryableExceptions(NonRetryableException.class);
        errorHandler.addRetryableExceptions(RetryableException.class);

        errorHandler.setRetryListeners(new RetryListener() {
            @Override
            public void failedDelivery(ConsumerRecord<?, ?> record, Exception ex, int deliveryAttempt) {
                log.info("РЕТРАЙ {} для сообщения: {}, ошибка: {}",
                        deliveryAttempt, record.key(), ex.getMessage());
            }
        });


        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;

    }

}