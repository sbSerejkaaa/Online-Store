package com.example.payment.repository;

import com.example.payment.model.entity.account.BankAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    BankAccount findByNumber(String accountNumber);

    @EntityGraph(attributePaths = "currencyAccounts")
    Optional<BankAccount> findByCustomerId(Long customerId);

}
