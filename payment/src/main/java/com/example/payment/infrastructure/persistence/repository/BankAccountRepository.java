package com.example.payment.infrastructure.persistence.repository;

import com.example.payment.infrastructure.persistence.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {

    Optional<BankAccount> findByCustomerId(UUID customerId);

}
