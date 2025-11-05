package com.example.payment.model.entity;

import com.example.payment.model.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
@Entity
@Table(name = "refund")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund extends BaseEntity {
    // ID возврата денежных средств

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;
    // Ссылка на исходный платеж, по которому делается возврат
    // Возврат всегда привязан к конкретному платежу

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;
    // Сумма возврата (может быть меньше исходного платежа)

    @Enumerated(EnumType.STRING)
    private RefundStatus status;
    // CREATED - заявка создана
    // PROCESSING - возврат в процессе
    // COMPLETED - деньги вернулись на счет
    // FAILED - ошибка возврата
}
