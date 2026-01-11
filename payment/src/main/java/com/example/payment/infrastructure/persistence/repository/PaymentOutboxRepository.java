package com.example.payment.infrastructure.persistence.repository;

import com.example.payment.infrastructure.persistence.outboxEntity.PaymentOutbox;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.time.Instant;
import java.util.List;


@Repository
public interface PaymentOutboxRepository extends JpaRepository<PaymentOutbox, Long> {
    // Метод для поиска PENDING событий
    List<PaymentOutbox> findByStatusAndCreatedAtBefore(
            PaymentOutbox.OutboxStatus status,
            Instant cutoffTime,
            Pageable pageable
    );
}
