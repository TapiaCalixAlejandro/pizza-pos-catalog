package com.msvc.catalog.dto.ingredient.request;

import com.msvc.catalog.enums.IngredientUnit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class IngredientRequest {

    @NotBlank(message = "Name is required.")
    @Size(max = 150, message = "Name must not exceed 150 characters.")
    private String name;

    @NotNull(message = "Unit is required.")
    private IngredientUnit unit;

    @NotNull(message = "Stock in required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Stock cannot be negative.")
    private BigDecimal stock;

    @NotNull(message = "Minimum stock in required.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum stock cannot be negative.")
    private BigDecimal minimumStock;

    @NotNull(message = "Cost is required.")
    @DecimalMin(value = "0.01", message = "Cost must be greater than zero.")
    private BigDecimal cost;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IngredientUnit getUnit() {
        return unit;
    }

    public void setUnit(IngredientUnit unit) {
        this.unit = unit;
    }

    public BigDecimal getStock() {
        return stock;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }

    public BigDecimal getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(BigDecimal minimumStock) {
        this.minimumStock = minimumStock;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

}
