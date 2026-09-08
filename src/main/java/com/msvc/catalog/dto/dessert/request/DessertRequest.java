package com.msvc.catalog.dto.dessert.request;

import com.msvc.catalog.enums.PortionUnit;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DessertRequest {

    @NotNull(message = "Product id is required.")
    @Positive(message = "Product id must be greater than zero.")
    private Long productId;

    @NotNull(message = "Portion is required.")
    @Positive(message = "Portion must be greater than zero.")
    private Integer portion;

    @NotNull(message = "Portion unit is required.")
    private PortionUnit portionUnit;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getPortion() {
        return portion;
    }

    public void setPortion(Integer portion) {
        this.portion = portion;
    }

    public PortionUnit getPortionUnit() {
        return portionUnit;
    }

    public void setPortionUnit(PortionUnit portionUnit) {
        this.portionUnit = portionUnit;
    }

}
