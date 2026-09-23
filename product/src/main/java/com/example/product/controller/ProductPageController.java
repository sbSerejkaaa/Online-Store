package com.example.product.controller;

import com.example.core.model.Product;
import com.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ProductPageController {

    private final ProductService productService;

    @GetMapping({"/", "/catalog"})
    public String showCatalog(Model model) {
        model.addAttribute("products", productService.findAll());
        return "catalog";
    }

    @GetMapping("/product/{id}")
    public String showProductDetail(@PathVariable UUID id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "product-detail";
    }

    // ★ ДОБАВЛЕНО: метод для страницы корзины
    @GetMapping("/cart")
    public String showCartPage() {
        return "cart";
    }

    @GetMapping("/orders")
    public String showOrdersPage() {
        return "orders";
    }
}