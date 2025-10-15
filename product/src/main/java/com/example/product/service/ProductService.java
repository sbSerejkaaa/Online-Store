package com.example.product.service;

import com.example.core.model.Orders;
import com.example.core.model.Product;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    Product createProduct(Product product);// заполняет админ
    Product reserve(Product desiredProduct, UUID orderId);
    void cancelReservation(Product productToCancel, UUID orderId);
    List<Product> findAll();
}
