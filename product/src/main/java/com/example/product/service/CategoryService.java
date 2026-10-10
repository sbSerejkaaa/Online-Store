package com.example.product.service;

import com.example.core.model.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    List<Category> findAll();

    Category findBySlug(String slug);

    Category findById(UUID id);
}