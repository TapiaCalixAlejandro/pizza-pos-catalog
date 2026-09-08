package com.msvc.catalog.dto.dessert.response;

import com.msvc.catalog.enums.PortionUnit;

public class DessertResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Integer portion;
    private PortionUnit portionUnit;

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
