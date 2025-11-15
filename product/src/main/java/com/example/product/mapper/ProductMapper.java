package com.example.product.mapper;

import com.example.core.event.product.ProductReservedEvent;
import com.example.core.model.Product;
import com.example.product.controller.dto.admin.ProductRegistrationRequest;
import com.example.product.controller.dto.admin.ProductResponse;
import com.example.product.entity.EntityProduct;
import java.time.Instant;

public class ProductMapper {

    public Product toCoreModel(ProductRegistrationRequest request){
        Product productModel = new Product();
        // В будущем нужен будет айди из Order
        productModel.setProductName(request.getNameProduct());
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

    public ProductReservedEvent toEvent(EntityProduct entityProduct){
        ProductReservedEvent productReservedEvent = new ProductReservedEvent();

        productReservedEvent.setInventoryId(entityProduct.getId());
        productReservedEvent.setOrderId(entityProduct.getId());
        productReservedEvent.setProductName(entityProduct.getProductName());
        productReservedEvent.setQuantity(entityProduct.getQuantity());
        productReservedEvent.setPrice(entityProduct.getPrice());
        //status
        productReservedEvent.setCreatedAt(entityProduct.getCreatedAt());

        return productReservedEvent;

    }


}
