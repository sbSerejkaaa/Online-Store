package com.example.authService.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(exclude = "password")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_name", nullable = false, length = 50)
    private String userName;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "registration_date", nullable = false, updatable = false)
    private Instant registrationDate;

    public Users( String userName, String email, String password){
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.registrationDate = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        if (registrationDate == null) {
            registrationDate = Instant.now();
        }
    }

}
