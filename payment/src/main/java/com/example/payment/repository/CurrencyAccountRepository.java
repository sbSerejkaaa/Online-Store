package com.example.payment.repository;

import com.example.payment.model.entity.account.CurrencyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyAccountRepository extends JpaRepository<CurrencyAccount, Long> {

    @Query(
            value = """
                      SELECT * 
                      FROM   currency_account
                      WHERE  bank_account_id = :accountId
                    """,
            nativeQuery = true
    )
    List<CurrencyAccount> findByBankAccount(@Param("accountId") Long accountId);


}
