package com.example.product.service;

import com.example.core.commandSaga.ReserveProductCommand;
import com.example.core.exception.ProductInsufficientQuantityException;
import com.example.core.model.Product;
import com.example.product.entity.EntityProduct;
import com.example.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ProductOutboxService outboxService;

    // МЕТОД ДЛЯ КЭШИРОВАНИЯ ТОВАРА
    // Храним в Redis по ключу "products::имя_товара"
    @Cacheable(value = "products", key = "#productName")
    public EntityProduct getCachedProduct(String productName) {
        log.info(" ЗАПРОС В БД для товара: {}", productName);
        return productRepository.findByProductName(productName)
                .orElseThrow(() -> new RuntimeException("Товар не найден: " + productName));
    }

    // ОЧИСТКА КЭША ПРИ ИЗМЕНЕНИИ ТОВАРА
    @CacheEvict(value = "products", key = "#productName")
    public void evictProductCache(String productName) {
        log.info("Кэш для товара {} очищен", productName);
    }


    @Override
    @Transactional
    public void reserveProduct(ReserveProductCommand command, BigDecimal totalAmount) {
        // 1. ЛОГИКА РЕЗЕРВАЦИИ
        EntityProduct productEntity = productRepository.findByProductName(command.getProductName())
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        if (command.getQuantity() > productEntity.getQuantity()) {
            throw new ProductInsufficientQuantityException(productEntity.getId(), null);
        }

        productEntity.setQuantity(productEntity.getQuantity() - command.getQuantity());
        productRepository.save(productEntity);

        log.info("Зарезервировано {} шт товара '{}'", command.getQuantity(), command.getProductName());

        // 2. СОХРАНЕНИЕ В OUTBOX (В ТОЙ ЖЕ ТРАНЗАКЦИИ)
        outboxService.saveProductReserved(command, productEntity.getId(), totalAmount);

        // 3. ОЧИЩАЕМ КЭШ, ТАК КАК КОЛИЧЕСТВО ИЗМЕНИЛОСЬ
        evictProductCache(command.getProductName());

        log.info("Outbox сохранен для order: {}", command.getOrderId());
    }

    @Override
    @Transactional
    public void cancelReservation(Product productToCancel, UUID orderId) {
        EntityProduct productEntity = productRepository.findById(productToCancel.getProductId()).orElseThrow();
        productEntity.setQuantity(productEntity.getQuantity() + productToCancel.getQuantity());
        productRepository.save(productEntity);

        // ОЧИЩАЕМ КЭШ, ТАК КАК КОЛИЧЕСТВО ИЗМЕНИЛОСЬ
        evictProductCache(productEntity.getProductName());

    }

    @Override
    @Transactional
    public List<Product> findAll() {
         return productRepository.findAll().stream()
                .map(entity -> new Product(
                        entity.getId(),
                        entity.getProductName(),
                        entity.getQuantity(),
                        entity.getPrice()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BigDecimal calculateTotalAmount(String productName, Integer quantity) {
        // 1. Берём товар из Redis (или БД при первом запросе)
        EntityProduct cachedProduct = getCachedProduct(productName);

        // 2. Свежий остаток — отдельный быстрый запрос в БД
        int actualQuantity = productRepository.getQuantityByName(productName);
        // ПРОВЕРКА + РАСЧЕТ
        if (quantity > actualQuantity) {
            throw new ProductInsufficientQuantityException(cachedProduct.getId(), null);
        }

        //  РАССЧИТЫВАЕМ СУММУ
        return cachedProduct.getPrice().multiply(new BigDecimal(quantity));

    }
}

