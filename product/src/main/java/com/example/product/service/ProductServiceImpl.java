package com.example.product.service;

import com.example.core.commandSaga.ReserveProductCommand;
import com.example.core.exception.ProductInsufficientQuantityException;
import com.example.core.model.Product;
import com.example.product.entity.EntityProduct;
import com.example.product.mapper.ProductMapper;
import com.example.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductOutboxService outboxService;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public void reserveProduct(ReserveProductCommand command,
                               BigDecimal unitPrice,
                               BigDecimal totalAmount) {

        EntityProduct productEntity = productRepository.findByProductName(command.getProductName())
                .orElseThrow(() -> new RuntimeException(
                        "Товар не найден: " + command.getProductName()));

        if (command.getQuantity() > productEntity.getQuantity()) {
            throw new ProductInsufficientQuantityException(
                    productEntity.getId(), command.getOrderId());
        }

        productEntity.setQuantity(productEntity.getQuantity() - command.getQuantity());
        productRepository.save(productEntity);

        log.info("Зарезервировано {} шт товара '{}'",
                command.getQuantity(), command.getProductName());

        outboxService.saveProductReserved(
                command,
                productEntity.getId(),
                unitPrice,
                totalAmount
        );
    }

    @Override
    @Transactional
    public void cancelReservation(Product productToCancel, UUID orderId) {
        EntityProduct productEntity = productRepository.findById(productToCancel.getProductId())
                .orElseThrow();
        productEntity.setQuantity(productEntity.getQuantity() + productToCancel.getQuantity());
        productRepository.save(productEntity);
    }

    @Override
    @Transactional
    public List<Product> findAll() {
        List<Product> products = productRepository.findAll().stream()
                .map(productMapper::toModel)
                .collect(Collectors.toList());
        Collections.shuffle(products);
        return products;
    }

    @Override
    @Transactional
    public Product findById(UUID id) {
        EntityProduct entity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не найден: " + id));
        return productMapper.toModel(entity);
    }

    @Override
    @Transactional
    public BigDecimal calculateUnitPrice(String productName) {
        EntityProduct product = productRepository.findByProductName(productName)
                .orElseThrow(() -> new RuntimeException("Товар не найден: " + productName));
        return product.getPrice();
    }

    @Override
    @Cacheable(value = "featured", key = "'top30'", sync = true)
    @Transactional(readOnly = true)
    public List<Product> findFeatured(int limit) {
        log.info("CACHE MISS: загрузка featured товаров из БД (top30)");
        return productRepository.findAll().stream()
                .limit(30)
                .map(productMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findByCategorySlug(String slug) {
        return productRepository.findByCategory_Slug(slug).stream()
                .map(productMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> searchByName(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        return productRepository.searchByName(query.trim()).stream()
                .map(productMapper::toModel)
                .collect(Collectors.toList());
    }
}