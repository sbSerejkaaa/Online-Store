package com.example.product.repository;

import com.example.product.entity.EntityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<EntityCategory, UUID> {

    Optional<EntityCategory> findBySlug(String slug);

    List<EntityCategory> findAllByOrderBySortOrderAsc();
}