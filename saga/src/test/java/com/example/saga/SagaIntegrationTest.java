package com.example.saga;

import com.example.core.event.order.OrderCreatedEvent;
import com.example.core.event.order.dto.OrderItemDto;
import com.example.saga.persistence.entity.SagaState;
import com.example.saga.persistence.repository.SagaOutboxRepository;
import com.example.saga.persistence.repository.SagaStateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class SagaIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("saga_db")
            .withUsername("postgres")
            .withPassword("12345qwert");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        // Отключаем Eureka
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.discovery.enabled", () -> "false");
    }

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private SagaStateRepository stateRepository;

    @Autowired
    private SagaOutboxRepository outboxRepository;

    @Test
    void orderCreatedEvent_ShouldStartSagaAndSendReserveCommand() throws Exception {
        // 1. Подготовка события
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        OrderItemDto item = OrderItemDto.builder()
                .productId(UUID.randomUUID())
                .productName("Test Product")
                .quantity(2)
                .unitPrice(new BigDecimal("100.00"))
                .build();

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(orderId)
                .userId(userId)
                .items(List.of(item))
                .build();

        // 2. Отправляем событие в Kafka
        kafkaTemplate.send("order.event.topic", orderId.toString(), event).get();

        // 3. Ждем, пока сага обработает (максимум 5 секунд)
        Thread.sleep(3000);

        // 4. Проверяем, что сага создалась в БД
        SagaState state = stateRepository.findById(orderId).orElse(null);
        assertNotNull(state, "Сага должна быть создана в БД!");
        assertEquals(SagaState.SagaStatus.RESERVING_PRODUCTS, state.getStatus());
        assertEquals(1, state.getTotalItems());

        // 5. Проверяем, что в outbox появилась команда на резервацию
        var outboxEvents = outboxRepository.findAll();
        boolean hasReserveCommand = outboxEvents.stream()
                .anyMatch(e -> e.getCommandType().equals("RESERVE_PRODUCT") && e.getAggregateId().equals(orderId));
        assertTrue(hasReserveCommand, "В outbox должна быть команда RESERVE_PRODUCT!");

        System.out.println("✅ Интеграционный тест пройден: Сага стартовала, статус = " + state.getStatus());
    }
}