package com.example.payment.infrastructure.persistence.repository;


import com.example.payment.infrastructure.persistence.entity.Refund;
import com.example.payment.infrastructure.persistence.enums.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RefundRepository extends JpaRepository<Refund, UUID> {

    // Найти возвраты по ID платежа
    List<Refund> findByPayment_Id(UUID paymentId);

    // Найти возвраты по ID заказа (через связь с платежом)
    List<Refund> findByPayment_OrderId(UUID orderId);

    // Найти возвраты по статусу
    List<Refund> findByStatus(RefundStatus status);
}
