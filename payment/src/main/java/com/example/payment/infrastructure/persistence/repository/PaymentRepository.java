package com.example.payment.infrastructure.persistence.repository;

import com.example.payment.infrastructure.persistence.entity.Payment;
import com.example.payment.infrastructure.persistence.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByOrderId(UUID orderId);

    List<Payment> findByBankAccount_CustomerId(UUID customerId);

    List<Payment> findByStatus(PaymentStatus status);

    boolean existsByOrderId(UUID orderId);
}
