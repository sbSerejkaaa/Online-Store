package com.example.product.repository;

import com.example.product.entity.EntityProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<EntityProduct, UUID> {

    Optional<EntityProduct> findByProductName(String productName);
}
