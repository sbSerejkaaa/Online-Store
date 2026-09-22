package com.example.saga.persistence.repository;

import com.example.saga.persistence.entity.SagaOutbox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SagaOutboxRepository extends JpaRepository<SagaOutbox, Long> {

    List<SagaOutbox> findByStatusAndCreatedAtBefore(
            SagaOutbox.OutboxStatus status,
            Instant cutoffTime,
            Pageable pageable
    );
}