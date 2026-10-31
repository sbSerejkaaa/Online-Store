package com.example.product.load;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class ProductLoadTest extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .userAgentHeader("Gatling/LoadTest");

    ScenarioBuilder scn = scenario("Catalog Load Test")
            .exec(
                    http("Get Products Page")
                            .get("/api/products/page?size=20")
                            .check(status().is(200))
            )
            .pause(Duration.ofMillis(500));

    {
        setUp(
                scn.injectOpen(
                        rampUsers(100).during(Duration.ofSeconds(10)),
                        constantUsersPerSec(100).during(Duration.ofSeconds(30))
                )
        ).protocols(httpProtocol);
    }
}