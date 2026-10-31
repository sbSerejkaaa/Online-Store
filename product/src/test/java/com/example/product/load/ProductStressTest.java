package com.example.product.load;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.time.Duration;
import java.util.Random;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class ProductStressTest extends Simulation {

    private static final Random random = new Random();
    private static String getRandomIp() {
        return random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256);
    }

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            // ★ Обходим Rate Limiter, подставляя случайный IP
            .header("X-Forwarded-For", session -> getRandomIp());

    ScenarioBuilder scn = scenario("Stress Test")
            .exec(
                    http("Get Products Page")
                            .get("/api/products/page?size=20")
                            .check(status().is(200))
            )
            .pause(Duration.ofMillis(100)); // Пауза 100мс

    {
        // ★ Умеренная нагрузка: 50 юзеров, держим 20 секунд
        setUp(
                scn.injectOpen(
                        rampUsers(50).during(Duration.ofSeconds(5)), // Разгон до 50 за 5 сек
                        constantUsersPerSec(50).during(Duration.ofSeconds(20)) // Держим 50 RPS
                )
        ).protocols(httpProtocol);
    }
}