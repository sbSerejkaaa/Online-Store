package com.example.product.repository;

import com.example.product.entity.EntityProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<EntityProduct, UUID> {

    Optional<EntityProduct> findByProductName(String productName);

    // 🔥 Новый метод — только количество (быстрый запрос)
    @Query("SELECT p.quantity FROM EntityProduct p WHERE p.productName = :productName")
    int getQuantityByName(String productName);
}
