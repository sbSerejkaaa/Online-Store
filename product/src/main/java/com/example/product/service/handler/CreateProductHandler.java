package com.example.product.service.handler;

import com.example.core.model.Product;
import com.example.product.entity.EntityCategory;
import com.example.product.entity.EntityProduct;
import com.example.product.mapper.ProductMapper;
import com.example.product.repository.CategoryRepository;
import com.example.product.repository.ProductRepository;
import com.example.product.service.command.AddProductOnWarehouseCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateProductHandler {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;   // ★ ДОБАВИЛИ
    private final ProductMapper productMapper;

    @Transactional
    public Product handle(AddProductOnWarehouseCommand command) {

        log.info("Добавление товара АДМИНОМ на сайт! {}", command.getNameProduct());

        EntityProduct entityProduct = new EntityProduct(
                command.getNameProduct(),
                command.getQuantity(),
                command.getPrice()
        );

        entityProduct.setDescription(command.getDescription());
        entityProduct.setImageUrl(command.getImageUrl());

        if (command.getCategorySlug() != null && !command.getCategorySlug().isBlank()) {
            EntityCategory category = categoryRepository.findBySlug(command.getCategorySlug())
                    .orElseThrow(() -> new RuntimeException(
                            "Категория не найдена: " + command.getCategorySlug()));
            entityProduct.setCategory(category);
            log.info("Товар привязан к категории '{}'", category.getName());
        }

        log.info("Сохранили в Бд наш товар: {}", entityProduct.getProductName());
        EntityProduct savedEntity = productRepository.save(entityProduct);

        return productMapper.toModel(savedEntity);
    }
}