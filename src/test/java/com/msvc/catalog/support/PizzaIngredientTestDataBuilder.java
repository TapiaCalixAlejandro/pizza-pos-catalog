package com.msvc.catalog.support;

import com.msvc.catalog.entity.Ingredient;
import com.msvc.catalog.entity.Pizza;
import com.msvc.catalog.entity.PizzaIngredient;

import java.math.BigDecimal;

public class PizzaIngredientTestDataBuilder {

    private Long id;
    private Pizza pizza;
    private Ingredient ingredient;
    private BigDecimal quantity = new BigDecimal("0.250");

    private PizzaIngredientTestDataBuilder() {
        this.ingredient = IngredientTestDataBuilder
                .anIngredient()
                .withId(1L)
                .withName("Mozzarella")
                .build();
    }

    public static PizzaIngredientTestDataBuilder aPizzaIngredient() {
        return new PizzaIngredientTestDataBuilder();
    }

    public PizzaIngredientTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public PizzaIngredientTestDataBuilder withPizza(Pizza pizza) {
        this.pizza = pizza;
        return this;
    }

    public PizzaIngredientTestDataBuilder withIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    public PizzaIngredientTestDataBuilder withQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        return this;
    }

    public PizzaIngredient build() {
        PizzaIngredient pizzaIngredient = new PizzaIngredient();
        pizzaIngredient.setId(id);
        pizzaIngredient.setPizza(pizza);
        pizzaIngredient.setIngredient(ingredient);
        pizzaIngredient.setQuantity(quantity);
        return pizzaIngredient;
    }

}
