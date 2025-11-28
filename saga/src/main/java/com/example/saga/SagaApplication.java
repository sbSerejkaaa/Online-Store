package com.example.saga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication
public class SagaApplication {

	public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SagaApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
	}

}
