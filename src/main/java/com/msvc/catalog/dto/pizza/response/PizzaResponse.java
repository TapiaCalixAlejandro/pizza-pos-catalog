package com.msvc.catalog.dto.pizza.response;

import java.util.List;

public class PizzaResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Integer preparationTime;
    private List<PizzaIngredientResponse> ingredients;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }

    public List<PizzaIngredientResponse> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<PizzaIngredientResponse> ingredients) {
        this.ingredients = ingredients;
    }

}
