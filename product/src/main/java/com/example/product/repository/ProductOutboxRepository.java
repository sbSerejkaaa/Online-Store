package com.example.product.repository;

import com.example.product.entity.ProductOutbox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ProductOutboxRepository extends JpaRepository<ProductOutbox, Long> {
    List<ProductOutbox> findByStatusAndCreatedAtBefore(
            ProductOutbox.OutboxStatus status,
            Instant cutoffTime,
            Pageable pageable
    );
}
