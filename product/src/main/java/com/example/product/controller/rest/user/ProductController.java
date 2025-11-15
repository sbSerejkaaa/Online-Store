package com.example.product.controller.rest.user;

import com.example.product.controller.dto.user.ProductCatalogDTO;
import com.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductCatalogDTO> getAllProducts() {
        log.info("📦 [PRODUCT API] Getting all products for catalog");
        return productService.getAllAvailableProducts();
    }

    @GetMapping("/{productName}")
    public ProductCatalogDTO getProductByName(@PathVariable String productName) {
        log.info("🔍 [PRODUCT API] Getting product by name: {}", productName);
        return productService.getProductDTOByName(productName);
    }
}
