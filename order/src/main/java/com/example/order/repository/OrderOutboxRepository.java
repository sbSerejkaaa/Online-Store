package com.example.order.repository;

import com.example.order.entity.OrderOutbox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OrderOutboxRepository extends JpaRepository<OrderOutbox, Long> {

    List<OrderOutbox> findByStatusAndCreatedAtBefore(
            OrderOutbox.OutboxStatus status,
            Instant cutoffTime,
            Pageable pageable
    );
}