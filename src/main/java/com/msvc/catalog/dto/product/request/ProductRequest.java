package com.msvc.catalog.dto.product.request;

import com.msvc.catalog.enums.ProductType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductRequest {

    @NotBlank(message = "Product name is required.")
    @Size(max = 150, message = "Product name must not exceed 150 characters.")
    private String name;

    @NotBlank(message = "Description is required.")
    @Size(max = 500, message = "Description must not exceed 500 characters.")
    private String description;

    @NotNull(message = "Price is required.")
    @Positive
    @DecimalMin(value = "0.01", message = "Price must be greater than zero.")
    private BigDecimal price;

    @Size(max = 255)
    private String image;

    @NotNull(message = "Product type is required.")
    private ProductType productType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

}
