package com.example.product.service;

import com.example.core.exception.ProductInsufficientQuantityException;
import com.example.core.model.Product;
import com.example.product.entity.EntityProduct;
import com.example.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    @Override
    public BigDecimal calculateTotalAmount(String productName, Integer quantity) {
        EntityProduct productEntity = productRepository.findByProductName(productName)
                .orElseThrow(() -> new RuntimeException("Товар не найден: " + productName));

        // ПРОВЕРКА + РАСЧЕТ
        if (quantity > productEntity.getQuantity()) {
            throw new ProductInsufficientQuantityException(productEntity.getId(), null);
        }

        // 🔥 РАССЧИТЫВАЕМ СУММУ
        return productEntity.getPrice().multiply(new BigDecimal(quantity));

    }
}

