package com.example.saga.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "saga_state")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SagaState {

    @Id
    @Column(name = "order_id", nullable = false, updatable = false)
    private UUID orderId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private SagaStatus status;

    @Column(name = "total_items", nullable = false)
    private Integer totalItems;

    @Column(name = "reserved_items", nullable = false)
    private Integer reservedItems;

    @Column(name = "failed_items", nullable = false)
    private Integer failedItems;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public enum SagaStatus {
        STARTED,              // сага стартовала, шлём ReserveProductCommand
        RESERVING_PRODUCTS,   // ждём ProductReservedEvent
        PRODUCTS_RESERVED,    // все товары зарезервированы, шлём CreatePaymentCommand
        PAYING,               // ждём PaymentCompletedEvent
        COMPLETED,            // заказ подтверждён
        COMPENSATING,         // откат
        FAILED                // сага упала
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (updatedAt == null) updatedAt = Instant.now();
        if (reservedItems == null) reservedItems = 0;
        if (failedItems == null) failedItems = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}