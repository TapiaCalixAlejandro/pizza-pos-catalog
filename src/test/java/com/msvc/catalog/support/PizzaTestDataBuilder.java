package com.msvc.catalog.support;

import com.msvc.catalog.dto.pizza.request.PizzaIngredientRequest;
import com.msvc.catalog.dto.pizza.request.PizzaRequest;
import com.msvc.catalog.dto.pizza.response.PizzaIngredientResponse;
import com.msvc.catalog.dto.pizza.response.PizzaResponse;
import com.msvc.catalog.entity.Pizza;
import com.msvc.catalog.entity.PizzaIngredient;
import com.msvc.catalog.entity.Product;
import com.msvc.catalog.enums.ProductType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PizzaTestDataBuilder {

    private Long id;
    private Product product;
    private Integer preparationTime           = 20;
    private List<PizzaIngredient> ingredients = new ArrayList<>();

    private PizzaTestDataBuilder() {
        // Producto por defecto (tipo PIZZA)
        this.product = ProductTestDataBuilder
                .aProduct()
                .withId(1L)
                .withName("Pizza Pepperoni")
                .withProductType(ProductType.PIZZA)
                .build();

        // Ingrediente por defecto (mínimo 1 para pasar @NotEmpty en PizzaRequest)
        this.ingredients.add(
                PizzaIngredientTestDataBuilder
                        .aPizzaIngredient()
                        .withIngredient(
                                IngredientTestDataBuilder
                                        .anIngredient()
                                        .withId(1L)
                                        .withName("Mozzarella")
                                        .build()
                        )
                        .withQuantity(new BigDecimal("0.250"))
                        .build()
        );
    }

    public static PizzaTestDataBuilder aPizza() {
        return new PizzaTestDataBuilder();
    }

    public PizzaTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public PizzaTestDataBuilder withProduct(Product product) {
        this.product = product;
        return this;
    }

    public PizzaTestDataBuilder withPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
        return this;
    }

    public PizzaTestDataBuilder withIngredients(List<PizzaIngredient> ingredients) {
        this.ingredients = ingredients;
        return this;
    }

    public PizzaTestDataBuilder addIngredient(PizzaIngredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    /**
     * Limpia la lista de ingredientes. Útil para tests que verifican el caso
     * de "pizza sin ingredientes" (lista vacía).
     */
    public PizzaTestDataBuilder withoutIngredients() {
        this.ingredients.clear();
        return this;
    }

    public Pizza build() {
        Pizza pizza = new Pizza();
        pizza.setId(id);
        pizza.setProduct(product);
        pizza.setPreparationTime(preparationTime);
        pizza.setIngredients(ingredients);
        return pizza;
    }

    public PizzaRequest buildRequest() {
        PizzaRequest request = new PizzaRequest();
        request.setProductId(product != null ? product.getId() : null);
        request.setPreparationTime(preparationTime);

        List<PizzaIngredientRequest> ingredientRequests = new ArrayList<>();
        for (PizzaIngredient ingredient : ingredients) {
            PizzaIngredientRequest ingReq = new PizzaIngredientRequest();
            ingReq.setIngredientId(
                    ingredient.getIngredient() != null
                            ? ingredient.getIngredient().getId()
                            : null
            );
            ingReq.setQuantity(ingredient.getQuantity());
            ingredientRequests.add(ingReq);
        }

        request.setIngredients(ingredientRequests);

        return request;
    }

    public PizzaResponse buildResponse() {
        PizzaResponse response = new PizzaResponse();
        response.setId(id);
        response.setProductId(product != null ? product.getId() : null);
        response.setProductName(product != null ? product.getName() : null);
        response.setPreparationTime(preparationTime);

        List<PizzaIngredientResponse> ingredientResponses = new ArrayList<>();
        for (PizzaIngredient ingredient : ingredients) {
            PizzaIngredientResponse ingResp = new PizzaIngredientResponse();
            ingResp.setIngredientId(
                    ingredient.getIngredient() != null
                            ? ingredient.getIngredient().getId()
                            : null
            );
            ingResp.setQuantity(ingredient.getQuantity());
            ingredientResponses.add(ingResp);
        }

        response.setIngredients(ingredientResponses);

        return response;
    }
}