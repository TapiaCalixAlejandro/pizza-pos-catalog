package com.msvc.catalog.support;

import com.msvc.catalog.dto.product.request.ProductRequest;
import com.msvc.catalog.dto.product.response.ProductResponse;
import com.msvc.catalog.entity.Product;
import com.msvc.catalog.enums.ProductStatus;
import com.msvc.catalog.enums.ProductType;

import java.math.BigDecimal;

public class ProductTestDataBuilder {

    private Long id;
    private String name                 = "Pizza Pepperoni";
    private String description          = "Classic Pepperoni";
    private BigDecimal price            = new BigDecimal("199.99");
    private String image                = "pepperoni.png";
    private ProductType productType     = ProductType.PIZZA;
    private ProductStatus productStatus = ProductStatus.ACTIVE;

    private ProductTestDataBuilder() {}

    public static ProductTestDataBuilder aProduct() {
        return new ProductTestDataBuilder();
    }

    public ProductTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ProductTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductTestDataBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public ProductTestDataBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ProductTestDataBuilder withImage(String image) {
        this.image = image;
        return this;
    }

    public ProductTestDataBuilder withProductType(ProductType productType) {
        this.productType = productType;
        return this;
    }

    public ProductTestDataBuilder withProductStatus(ProductStatus productStatus) {
        this.productStatus = productStatus;
        return this;
    }

    public Product build() {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setImage(image);
        product.setProductType(productType);
        product.setProductStatus(productStatus);
        return product;
    }

    public ProductRequest buildRequest() {
        ProductRequest request = new ProductRequest();
        request.setName(name);
        request.setDescription(description);
        request.setPrice(price);
        request.setImage(image);
        request.setProductType(productType);
        return request;
    }

    public ProductResponse buildResponse() {
        ProductResponse response = new ProductResponse();
        response.setId(id);
        response.setName(name);
        response.setDescription(description);
        response.setPrice(price);
        response.setImage(image);
        response.setProductType(productType);
        response.setProductStatus(productStatus);
        return response;
    }

}
