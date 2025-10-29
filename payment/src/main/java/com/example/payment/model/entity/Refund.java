package com.example.payment.model.entity;

import com.example.payment.model.enums.RefundStatus;
import com.example.payment.model.enums.converter.RefundStatusConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Entity
@Table(name = "refund")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Refund extends BaseEntity {
    private BigDecimal refundedAmount;

    @Convert(converter = RefundStatusConverter.class)
    private RefundStatus status;

    private String reason;

    @ManyToOne
    @JoinColumn(name = "paymentTransactionId", referencedColumnName = "id", nullable = false)
    private PaymentTransaction paymentTransaction;

}
