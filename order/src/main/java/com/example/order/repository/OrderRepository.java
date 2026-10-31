package com.example.order.repository;

import com.example.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    @EntityGraph(attributePaths = {"items"})
    Optional<OrderEntity> findById(UUID id);

    @EntityGraph(attributePaths = {"items"})
    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);

    long countByUserId(UUID userId);
}