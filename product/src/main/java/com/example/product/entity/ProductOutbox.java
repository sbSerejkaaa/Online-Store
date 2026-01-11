package com.example.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "product_outbox")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;  // PRODUCT_RESERVED, PRODUCT_RESERVATION_RELEASED, etc.

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;  // orderId

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    public enum OutboxStatus {
        PENDING, SENT, FAILED
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (status == null) {
            status = OutboxStatus.PENDING;
        }
        if (eventId == null) {
            eventId = UUID.randomUUID();
        }
    }
}
