package com.msvc.catalog.support;

import com.msvc.catalog.dto.dessert.request.DessertRequest;
import com.msvc.catalog.dto.dessert.response.DessertResponse;
import com.msvc.catalog.entity.Dessert;
import com.msvc.catalog.entity.Product;
import com.msvc.catalog.enums.PortionUnit;
import com.msvc.catalog.enums.ProductType;

public class DessertTestDataBuilder {

    private Long id;
    private Product product;
    private Integer portion = 1;
    private PortionUnit portionUnit = PortionUnit.PIECE;

    private DessertTestDataBuilder() {
        // Producto por defecto tipo DESSERT
        this.product = ProductTestDataBuilder
                .aProduct()
                .withId(1L)
                .withName("Pastel de Chocolate")
                .withDescription("Rebanada de pastel de chocolate")
                .withProductType(ProductType.DESSERT)
                .build();
    }

    public static DessertTestDataBuilder aDessert() {
        return new DessertTestDataBuilder();
    }

    public DessertTestDataBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public DessertTestDataBuilder withProduct(Product product) {
        this.product = product;
        return this;
    }

    public DessertTestDataBuilder withPortion(Integer portion) {
        this.portion = portion;
        return this;
    }

    public DessertTestDataBuilder withPortionUnit(PortionUnit portionUnit) {
        this.portionUnit = portionUnit;
        return this;
    }

    public Dessert build() {
        Dessert dessert = new Dessert();
        dessert.setId(id);
        dessert.setProduct(product);
        dessert.setPortion(portion);
        dessert.setPortionUnit(portionUnit);
        return dessert;
    }

    public DessertRequest buildRequest() {
        DessertRequest request = new DessertRequest();
        request.setProductId(product != null ? product.getId() : null);
        request.setPortion(portion);
        request.setPortionUnit(portionUnit);
        return request;
    }

    public DessertResponse buildResponse() {
        DessertResponse response = new DessertResponse();
        response.setId(id);
        response.setProductId(product != null ? product.getId() : null);
        response.setProductName(product != null ? product.getName() : null);
        response.setPortion(portion);
        response.setPortionUnit(portionUnit);
        return response;
    }
}