package com.example.product.service;

import com.example.core.model.Orders;
import com.example.core.model.Product;
import com.example.product.controller.dto.user.ProductCatalogDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    // Admin
    void reserveProduct(String productName, Integer quantity);
    void cancelReservation(Product productToCancel, UUID orderId);
    List<Product> findAll();

    // User
    List<ProductCatalogDTO> getAllAvailableProducts();
    ProductCatalogDTO getProductDTOByName(String productName);
}
