package com.msvc.catalog.dto.pizza.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class PizzaRequest {

    @NotNull(message = "Product id is required.")
    @Positive(message = "Product id must be greater then zero.")
    private Long productId;

    @NotNull(message = "Preparation time is required.")
    @Positive(message = "Preparation time must be greater than zero.")
    private Integer preparationTime;

    @NotEmpty(message = "Ingredients are required.")
    @Valid
    private List<PizzaIngredientRequest> ingredients;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }

    public List<PizzaIngredientRequest> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<PizzaIngredientRequest> ingredients) {
        this.ingredients = ingredients;
    }

}
