package com.example.core.event.users;

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

    // ОСНОВНЫЕ ДАННЫЕ
    private UUID userId;
    private String email;
    private String userName;
    private Instant registrationDate;

    // МЕТАДАННЫЕ СОБЫТИЯ
    private String eventId;
    private Instant eventTimestamp;

    // ★ Убрали final — теперь Jackson может десериализовать
    private String eventType = "USER_REGISTERED";
    private String eventVersion = "1.0";

    public UserRegisteredEvent(UUID userId, String email, String userName, Instant registrationDate) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.registrationDate = registrationDate;
        this.eventId = UUID.randomUUID().toString();
        this.eventTimestamp = Instant.now();
        this.eventType = "USER_REGISTERED";
        this.eventVersion = "1.0";
    }

    // ★ Инициализация в @PrePersist-стиле — Lombok @NoArgsConstructor вызовет пустой конструктор,
    //   поэтому дефолты надо ставить здесь, но так как это POJO — делаем через @JsonIgnore?
    //   Нет — просто полагаемся на AllArgsConstructor или кастомный конструктор.
}
