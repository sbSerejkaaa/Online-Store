package com.example.product.controller.rest.user;

import com.example.core.model.Product;
import com.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Публичный endpoint для главной страницы — хиты продаж.
     * Возвращает первые N товаров.
     */
    @GetMapping("/featured")
    public List<Product> getFeatured(@RequestParam(defaultValue = "6") int limit) {
        log.debug("Запрос featured товаров, limit={}", limit);
        return productService.findFeatured(limit);
    }
}