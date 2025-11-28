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
    // Также передается ID из BaseEntity, который является айди кошелька пользователя

    @NotNull
    @Column(name = "customer_id")
    private UUID customerId;
    // ID пользователя в вашей системе
    // Связывает банковский счет с конкретным клиентом


    @Column(name = "balance", precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
    // Текущий баланс счета в рублях
    // precision=19, scale=2 = 17 цифр до запятой, 2 после

    @Enumerated(EnumType.STRING)
    private BankAccountStatus status = BankAccountStatus.ACTIVE;
    // Статус счета:
    // ACTIVE - можно проводить операции
    // BLOCKED - временно заблокирован
    // CLOSED - закрыт навсегда

    @OneToMany(mappedBy = "bankAccount", cascade = CascadeType.ALL)
    private List<Payment> payments;
}