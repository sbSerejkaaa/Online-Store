package com.example.product.service;

import com.example.core.commandSaga.ReserveProductCommand;
import com.example.core.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductService {

    // Admin
    void reserveProduct(ReserveProductCommand command, BigDecimal totalAmount);

    void cancelReservation(Product productToCancel, UUID orderId);

    List<Product> findAll();

    BigDecimal calculateTotalAmount(String productName, Integer quantity);

}
