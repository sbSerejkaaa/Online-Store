package com.example.product.controller;

import com.example.core.model.Product;
import com.example.product.service.CategoryService;
import com.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ProductPageController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping({"/", "/catalog"})
    public String showCatalog(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            Model model) {

        List<Product> products;

        if (q != null && !q.isBlank()) {
            // ★ Поиск по имени — приоритет
            products = productService.searchByName(q);
            model.addAttribute("searchQuery", q);
            model.addAttribute("activeCategory", null);
        } else if (category == null || category.isBlank()) {
            products = productService.findAll();
            model.addAttribute("activeCategory", null);
        } else {
            products = productService.findByCategorySlug(category);
            model.addAttribute("activeCategory", category);
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.findAll());
        return "catalog";
    }

    @GetMapping("/product/{id}")
    public String showProductDetail(@PathVariable UUID id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "product-detail";
    }

    @GetMapping("/cart")
    public String showCartPage() {
        return "cart";
    }

    @GetMapping("/orders")
    public String showOrdersPage() {
        return "orders";
    }
}