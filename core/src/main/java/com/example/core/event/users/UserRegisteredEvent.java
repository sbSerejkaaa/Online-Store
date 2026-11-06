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

    private UUID userId;
    private String email;
    private String userName;
    private Instant registrationDate;

    private String eventId;
    private Instant eventTimestamp;

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
}
