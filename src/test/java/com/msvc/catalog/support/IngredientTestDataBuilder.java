package com.msvc.catalog.support;

import com.msvc.catalog.dto.ingredient.request.IngredientRequest;
import com.msvc.catalog.dto.ingredient.response.IngredientResponse;
import com.msvc.catalog.entity.Ingredient;
import com.msvc.catalog.enums.IngredientStatus;
import com.msvc.catalog.enums.IngredientUnit;

import java.math.BigDecimal;

public class IngredientTestDataBuilder {

    private Long id;
    private String name             = "Mozzarella";
    private IngredientUnit unit     = IngredientUnit.KILOGRAM;
    private BigDecimal stock        = new BigDecimal("20");
    private BigDecimal minimumStock = new BigDecimal("5");
    private BigDecimal cost         = new BigDecimal("180.00");
    private IngredientStatus status = IngredientStatus.ACTIVE;

    private IngredientTestDataBuilder() {}

    public static IngredientTestDataBuilder anIngredient() {
        return new IngredientTestDataBuilder();
    }

    public IngredientTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public IngredientTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public IngredientTestDataBuilder withUnit(IngredientUnit unit) {
        this.unit = unit;
        return this;
    }

    public IngredientTestDataBuilder withStock(BigDecimal stock) {
        this.stock = stock;
        return this;
    }

    public IngredientTestDataBuilder withMinimumStock(BigDecimal minimumStock) {
        this.minimumStock = minimumStock;
        return this;
    }

    public IngredientTestDataBuilder withCost(BigDecimal cost) {
        this.cost = cost;
        return this;
    }

    public IngredientTestDataBuilder withStatus(IngredientStatus status) {
        this.status = status;
        return this;
    }

    public Ingredient build() {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName(name);
        ingredient.setUnit(unit);
        ingredient.setStock(stock);
        ingredient.setMinimumStock(minimumStock);
        ingredient.setCost(cost);
        ingredient.setStatus(status);
        return ingredient;
    }

    public IngredientRequest buildRequest() {
        IngredientRequest request = new IngredientRequest();
        request.setName(name);
        request.setUnit(unit);
        request.setStock(stock);
        request.setMinimumStock(minimumStock);
        request.setCost(cost);
        return request;
    }

    public IngredientResponse buildResponse() {
        IngredientResponse response = new IngredientResponse();
        response.setId(id);
        response.setName(name);
        response.setUnit(unit);
        response.setStock(stock);
        response.setMinimumStock(minimumStock);
        response.setCost(cost);
        response.setStatus(status);
        return response;
    }

}
