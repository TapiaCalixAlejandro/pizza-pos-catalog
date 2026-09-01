package com.msvc.catalog.dto.pizza.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class PizzaIngredientRequest {

    @NotNull(message = "Ingredient id is required.")
    @Positive(message = "Ingredient id must be greater than zero.")
    private Long ingredientId;

    @NotNull(message = "Quantity is required.")
    @DecimalMin(value = "0.001", message = "Quantity must be greater than zero.")
    private BigDecimal quantity;

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

}
