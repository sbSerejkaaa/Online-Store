package com.example.product.controller.rest.user;

import com.example.core.model.Category;
import com.example.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<Category> getAll() {
        log.debug("Запрос всех категорий");
        return categoryService.findAll();
    }

    @GetMapping("/{slug}")
    public Category getBySlug(@PathVariable String slug) {
        log.debug("Запрос категории по slug: {}", slug);
        return categoryService.findBySlug(slug);
    }
}