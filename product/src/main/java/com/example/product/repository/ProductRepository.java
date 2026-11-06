package com.example.product.repository;

import com.example.product.entity.EntityProduct;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<EntityProduct, UUID> {

    @EntityGraph(attributePaths = {"category"})
    Optional<EntityProduct> findByProductName(String productName);

    @Query("SELECT p.quantity FROM EntityProduct p WHERE p.productName = :productName AND p.deletedAt IS NULL")
    int getQuantityByName(@Param("productName") String productName);

    @EntityGraph(attributePaths = {"category"})
    List<EntityProduct> findByCategory_Slug(String slug);

    @EntityGraph(attributePaths = {"category"})
    @Query(value = "SELECT * FROM product WHERE search_vector @@ plainto_tsquery('russian', :query) AND deleted_at IS NULL ORDER BY ts_rank(search_vector, plainto_tsquery('russian', :query)) DESC",
            nativeQuery = true)
    List<EntityProduct> searchByName(@Param("query") String query);

    @Override
    @EntityGraph(attributePaths = {"category"})
    List<EntityProduct> findAll();

    @Override
    @EntityGraph(attributePaths = {"category"})
    Optional<EntityProduct> findById(UUID id);

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT p FROM EntityProduct p WHERE p.createdAt < :cursor AND p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    List<EntityProduct> findPageByCursor(@Param("cursor") Instant cursor, Pageable pageable);
}