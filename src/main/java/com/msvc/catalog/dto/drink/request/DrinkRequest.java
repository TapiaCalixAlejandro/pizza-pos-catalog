package com.msvc.catalog.dto.drink.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DrinkRequest {

    @NotNull(message = "Product id is required.")
    @Positive(message = "Product id must be greater then zero.")
    private Long productId;

    @NotNull(message = "Volume is required.")
    @Positive(message = "Volume must be greater then zero.")
    private Integer volume;

    @NotNull(message = "Alcoholic status is required.")
    private Boolean alcoholic;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public Boolean getAlcoholic() {
        return alcoholic;
    }

    public void setAlcoholic(Boolean alcoholic) {
        this.alcoholic = alcoholic;
    }
}
