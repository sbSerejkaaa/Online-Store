package com.example.product.repository;

import com.example.product.entity.EntityProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<EntityProduct, UUID> {

    Optional<EntityProduct> findByProductName(String productName);

    @Query("SELECT p.quantity FROM EntityProduct p WHERE p.productName = :productName")
    int getQuantityByName(String productName);

    List<EntityProduct> findByCategory_Slug(String slug);

    // ★ НОВОЕ: поиск по имени
    @Query("SELECT p FROM EntityProduct p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<EntityProduct> searchByName(@Param("query") String query);
}