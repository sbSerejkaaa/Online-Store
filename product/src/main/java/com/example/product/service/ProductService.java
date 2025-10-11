package com.example.product.service;

import com.example.core.model.Orders;
import com.example.core.model.Product;

public interface ProductService {

    Product createProduct(Product product);// заполняет админ
    Product save(Product product);
}
