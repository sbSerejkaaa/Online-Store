package com.example.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {
    //  ОСНОВНЫЕ ДАННЫЕ (бизнес-логика)
    private UUID userId;
    private String email;
    private String userName;
    private Instant registrationDate;

    //  МЕТАДАННЫЕ СОБЫТИЯ (техническая информация)
    private String eventId = UUID.randomUUID().toString();     // Уникальный ID события
    private Instant eventTimestamp = Instant.now();           // Время создания события
    private final String EVENT_TYPE = "USER_REGISTERED";             // Тип события
    private final String EVENT_VERSION = "1.0";

    public UserRegisteredEvent(UUID userId, String email, String userName, Instant registrationDate) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.registrationDate = registrationDate;
        this.eventId = UUID.randomUUID().toString();
        this.eventTimestamp = Instant.now();
        // eventId и eventTimestamp инициализируются автоматически
    }
}
