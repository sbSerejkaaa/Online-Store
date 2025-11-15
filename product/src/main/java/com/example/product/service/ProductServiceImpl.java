package com.example.product.service;

import com.example.core.exception.ProductInsufficientQuantityException;
import com.example.core.model.Product;

import com.example.product.controller.dto.user.ProductCatalogDTO;
import com.example.product.entity.EntityProduct;
import com.example.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    @Override
    public void reserveProduct(String productName, Integer quantity) {
        EntityProduct productEntity = productRepository.findByProductName(productName)
                .orElseThrow(() -> new RuntimeException("Товар не найден: " + productName));

        if (quantity > productEntity.getQuantity()) {
            throw new ProductInsufficientQuantityException(productEntity.getId(), null);
        }

        productEntity.setQuantity(productEntity.getQuantity() - quantity);
        productRepository.save(productEntity);

        log.info("✅ [PRODUCT] Зарезервировано {} шт товара '{}'", quantity, productName);
    }

    @Override
    public void cancelReservation(Product productToCancel, UUID orderId) {
        EntityProduct productEntity = productRepository.findById(productToCancel.getProductId()).orElseThrow();
        productEntity.setQuantity(productEntity.getQuantity() + productToCancel.getQuantity());
        productRepository.save(productEntity);

    }

    @Override
    public List<Product> findAll() {
         return productRepository.findAll().stream()
                .map(entity -> new Product(entity.getId(), entity.getProductName(), entity.getQuantity(), entity.getPrice()))
                .collect(Collectors.toList());
    }


    // User method

    @Override
    public List<ProductCatalogDTO> getAllAvailableProducts() {
        log.info("Получение всех доступных товаров для каталога");

        return productRepository.findAll().stream()
                .filter(entity -> entity.getQuantity() > 0) // только товары в наличии
                .map(entity -> ProductCatalogDTO.builder()
                        .productName(entity.getProductName())
                        .price(entity.getPrice())
                        .availableQuantity(entity.getQuantity())
                        .description("") // можно добавить поле в EntityProduct позже
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ProductCatalogDTO getProductDTOByName(String productName) {
        log.info(" Получение DTO продукта по названию:: {}", productName);

        EntityProduct entity = productRepository.findByProductName(productName)
                .orElseThrow(() -> new RuntimeException("Товар не найден: " + productName));

        return ProductCatalogDTO.builder()
                .productName(entity.getProductName())
                .price(entity.getPrice())
                .availableQuantity(entity.getQuantity())
                .description("")
                .build();
    }

}

