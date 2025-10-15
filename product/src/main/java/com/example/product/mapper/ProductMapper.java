package com.example.product.mapper;

import com.example.core.event.ProductRegisteredEvent;
import com.example.core.model.Product;
import com.example.core.status.ProductStatus;
import com.example.product.dto.ProductRegistrationRequest;
import com.example.product.dto.ProductResponse;
import com.example.product.entity.EntityProduct;
import java.time.Instant;
import java.util.UUID;

public class ProductMapper {

    public Product toCoreModel(ProductRegistrationRequest request){
        Product productModel = new Product();
        // В будущем нужен будет айди из Order
        productModel.setProductName(request.getNameInventory());
        productModel.setQuantity(request.getQuantity());
        productModel.setPrice(request.getPrice());
        return productModel;
    }

    public EntityProduct toEntity(Product productModel){
        EntityProduct entityProduct = new EntityProduct();

        entityProduct.setProductName(productModel.getProductName());
        entityProduct.setQuantity(productModel.getQuantity());
        entityProduct.setPrice(productModel.getPrice());
        entityProduct.setCreatedAt(Instant.now());
        //  entityProduct.setStatus(); разобраться с логикой статуса
        return entityProduct;
    }

    public Product toModel(EntityProduct entityProduct){
        Product productModel = new Product();

        productModel.setProductId(entityProduct.getId());
        productModel.setProductName(entityProduct.getProductName());
        productModel.setQuantity(entityProduct.getQuantity());
        productModel.setPrice(entityProduct.getPrice());
        // Status
        return productModel;

    }

    public ProductResponse toResponse(Product productModel){
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(productModel.getProductId());
        productResponse.setNameInventory(productModel.getProductName());
        productResponse.setQuantity(productModel.getQuantity());
        productResponse.setPrice(productModel.getPrice());
        // status

        return productResponse;

    }

    public ProductRegisteredEvent toEvent(EntityProduct entityProduct){
        ProductRegisteredEvent productRegisteredEvent = new ProductRegisteredEvent();

        productRegisteredEvent.setInventoryId(entityProduct.getId());
        productRegisteredEvent.setOrderId(entityProduct.getId());
        productRegisteredEvent.setProductName(entityProduct.getProductName());
        productRegisteredEvent.setQuantity(entityProduct.getQuantity());
        productRegisteredEvent.setPrice(entityProduct.getPrice());
        //status
        productRegisteredEvent.setCreatedAt(entityProduct.getCreatedAt());

        return productRegisteredEvent;

    }


}
