package com.example.product.mapper;

import com.example.core.model.Category;
import com.example.product.controller.dto.admin.CategoryResponse;
import com.example.product.entity.EntityCategory;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toModel(EntityCategory entity) {
        if (entity == null) return null;
        return Category.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .icon(entity.getIcon())
                .sortOrder(entity.getSortOrder())
                .build();
    }

    public CategoryResponse toResponse(EntityCategory entity) {
        if (entity == null) return null;
        return CategoryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .icon(entity.getIcon())
                .sortOrder(entity.getSortOrder())
                .build();
    }
}