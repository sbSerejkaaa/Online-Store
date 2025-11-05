package com.example.payment.model.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Версия для оптимистичной блокировки
    // Увеличивается при каждом обновлении
    // Защищает от одновременного изменения
    @Version
    private Long version;

    // Когда запись создана (автоматически при INSERT)
    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    // Когда запись последний раз обновлена (автоматически при UPDATE)
    @UpdateTimestamp
    private Instant updatedAt;

}
