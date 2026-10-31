package com.example.order;

import com.example.core.status.OrderStatus;
import com.example.order.entity.OrderEntity;
import com.example.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@TestPropertySource(properties = {
        "api.version=1.44"
})
@SpringBootTest
@Testcontainers
class OrderE2ETest {

    // 1. Поднимаем настоящий Postgres
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("order_db")
            .withUsername("postgres")
            .withPassword("12345qwert");

    // 2. Поднимаем настоящую Kafka
    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    // 3. Подменяем конфиг Spring Boot на порты этих контейнеров
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        // Отключаем Eureka, чтобы тест не искал Discovery Server
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.discovery.enabled", () -> "false");
    }

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void contextLoadsAndDbWorks() {
        // Проверяем, что БД реально работает
        assertNotNull(orderRepository);

        // Создаем тестовый заказ
        OrderEntity order = OrderEntity.builder()
                .userId(UUID.randomUUID())
                .totalAmount(new BigDecimal("1500.00"))
                .status(OrderStatus.IN_PROCESS)
                .createdAt(Instant.now())
                .build();

        // Сохраняем в настоящую БД
        OrderEntity saved = orderRepository.save(order);

        // Проверяем, что он сохранился и получил ID
        assertNotNull(saved.getId());
        assertEquals(1, orderRepository.count());

        System.out.println("✅ Testcontainers: Postgres и Kafka успешно запущены! Заказ сохранен с ID: " + saved.getId());
    }
}