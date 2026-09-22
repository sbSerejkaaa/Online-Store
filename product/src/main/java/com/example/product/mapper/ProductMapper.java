package com.example.product.mapper;

import com.example.core.model.Product;
import com.example.product.controller.dto.admin.ProductRegistrationRequest;
import com.example.product.controller.dto.admin.ProductResponse;
import com.example.product.entity.EntityProduct;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ProductMapper {

    public Product toCoreModel(ProductRegistrationRequest request) {
        Product productModel = new Product();
        productModel.setProductName(request.getNameProduct());
        productModel.setQuantity(request.getQuantity());
        productModel.setPrice(request.getPrice());
        return productModel;
    }

    public EntityProduct toEntity(Product productModel) {
        EntityProduct entityProduct = new EntityProduct();
        entityProduct.setProductName(productModel.getProductName());
        entityProduct.setQuantity(productModel.getQuantity());
        entityProduct.setPrice(productModel.getPrice());
        entityProduct.setCreatedAt(Instant.now());
        return entityProduct;
    }

    public Product toModel(EntityProduct entityProduct) {
        Product productModel = new Product();
        productModel.setProductId(entityProduct.getId());
        productModel.setProductName(entityProduct.getProductName());
        productModel.setDescription(entityProduct.getDescription());
        productModel.setImageUrl(entityProduct.getImageUrl());
        productModel.setQuantity(entityProduct.getQuantity());
        productModel.setPrice(entityProduct.getPrice());
        return productModel;
    }

    public ProductResponse toResponse(Product productModel) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(productModel.getProductId());
        productResponse.setNameInventory(productModel.getProductName());
        productResponse.setQuantity(productModel.getQuantity());
        productResponse.setPrice(productModel.getPrice());
        return productResponse;
    }
}