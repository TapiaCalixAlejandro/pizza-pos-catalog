package com.msvc.catalog.support;

import com.msvc.catalog.dto.drink.request.DrinkRequest;
import com.msvc.catalog.dto.drink.response.DrinkResponse;
import com.msvc.catalog.entity.Drink;
import com.msvc.catalog.entity.Product;
import com.msvc.catalog.enums.ProductType;

public class DrinkTestDataBuilder {

    private Long id;
    private Product product;
    private Integer volume = 500;
    private Boolean alcoholic = false;

    private DrinkTestDataBuilder() {
        // Producto por defecto tipo DRINK
        this.product = ProductTestDataBuilder
                .aProduct()
                .withId(1L)
                .withName("Coca Cola")
                .withDescription("Refresco de cola 500ml")
                .withProductType(ProductType.DRINK)
                .build();
    }

    public static DrinkTestDataBuilder aDrink() {
        return new DrinkTestDataBuilder();
    }

    public DrinkTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public DrinkTestDataBuilder withProduct(Product product) {
        this.product = product;
        return this;
    }

    public DrinkTestDataBuilder withVolume(Integer volume) {
        this.volume = volume;
        return this;
    }

    public DrinkTestDataBuilder withAlcoholic(Boolean alcoholic) {
        this.alcoholic = alcoholic;
        return this;
    }

    public Drink build() {
        Drink drink = new Drink();
        drink.setId(id);
        drink.setProduct(product);
        drink.setVolume(volume);
        drink.setAlcoholic(alcoholic);
        return drink;
    }

    public DrinkRequest buildRequest() {
        DrinkRequest request = new DrinkRequest();
        request.setProductId(product != null ? product.getId() : null);
        request.setVolume(volume);
        request.setAlcoholic(alcoholic);
        return request;
    }

    public DrinkResponse buildResponse() {
        DrinkResponse response = new DrinkResponse();
        response.setId(id);
        response.setProductId(product != null ? product.getId() : null);
        response.setProductName(product != null ? product.getName() : null);
        response.setVolume(volume);
        response.setAlcoholic(alcoholic);
        return response;
    }
}