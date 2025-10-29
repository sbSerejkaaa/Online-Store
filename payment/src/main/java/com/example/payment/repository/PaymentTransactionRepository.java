package com.example.payment.repository;

import com.example.payment.model.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
}
