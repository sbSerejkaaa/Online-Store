package com.example.authService;

import com.example.authService.dto.UserRegistrationRequest;
import com.example.authService.dto.UserResponse;
import com.example.authService.repository.UserRepository;
import com.example.authService.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class AuthServiceApplicationTests {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
			.withDatabaseName("auth_db").withUsername("postgres").withPassword("test");

	@Container
	static KafkaContainer kafka = new KafkaContainer(
			DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

	@Container
	static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
			.withExposedPorts(6379)
			.waitingFor(Wait.forListeningPort());

	@DynamicPropertySource
	static void props(DynamicPropertyRegistry r) {
		r.add("spring.datasource.url", postgres::getJdbcUrl);
		r.add("spring.datasource.username", postgres::getUsername);
		r.add("spring.datasource.password", postgres::getPassword);
		r.add("spring.data.redis.host", redis::getHost);
		r.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
		r.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
	}

	@Autowired UserService userService;
	@Autowired UserRepository userRepository;

	@BeforeEach
	void clean() {
		userRepository.deleteAll();
	}

	@Test
	void contextLoads() {
		assertThat(userService).isNotNull();
	}

	@Test
	void registerUser_persists() {
		UserResponse r = userService.registeredUser(
				new UserRegistrationRequest("TestUser", "test@example.com", "Password123!"));

		assertThat(r.getId()).isNotNull();
		assertThat(userRepository.findByEmail("test@example.com")).isPresent();
	}

	@Test
	void duplicateEmail_throws() {
		UserRegistrationRequest req =
				new UserRegistrationRequest("TestUser", "dup@example.com", "Password123!");
		userService.registeredUser(req);

		assertThrows(RuntimeException.class, () -> userService.registeredUser(req));
	}
}