package com.example.product.service;

import com.example.core.model.Category;
import com.example.product.entity.EntityCategory;
import com.example.product.mapper.CategoryMapper;
import com.example.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAllByOrderBySortOrderAsc().stream()
                .map(categoryMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Category findBySlug(String slug) {
        EntityCategory entity = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Категория не найдена: " + slug));
        return categoryMapper.toModel(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Category findById(UUID id) {
        EntityCategory entity = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена: " + id));
        return categoryMapper.toModel(entity);
    }
}