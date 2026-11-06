package com.example.payment.infrastructure.persistence.entity;

import com.example.payment.infrastructure.persistence.enums.BankAccountStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bank_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccount extends BaseEntity {

    @NotNull
    @Column(name = "customer_id")
    private UUID customerId;

    @Builder.Default
    @Column(name = "balance", precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private BankAccountStatus status = BankAccountStatus.ACTIVE;

    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL)
    private List<Payment> payments;
}