package com.example.authService.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(exclude = "password")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false, length = 50)
    private String userName;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String eMail;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    public Users( String userName, String eMail, String password){
        this.userName = userName;
        this.eMail = eMail;
        this.password = password;
        this.registrationDate = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (registrationDate == null) {
            registrationDate = LocalDateTime.now();
        }
    }

    private String LastName;
}
