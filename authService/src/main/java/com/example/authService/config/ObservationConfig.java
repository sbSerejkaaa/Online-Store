package com.example.authService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.observation.SecurityObservationSettings;

@Configuration
public class ObservationConfig {

    @Bean
    public SecurityObservationSettings securityObservationSettings() {
        return SecurityObservationSettings.withDefaults()
                .shouldObserveRequests(true)
                .build();
    }
}