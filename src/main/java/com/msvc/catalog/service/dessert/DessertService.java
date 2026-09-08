package com.msvc.catalog.service.dessert;

import com.msvc.catalog.dto.dessert.request.DessertRequest;
import com.msvc.catalog.dto.dessert.response.DessertResponse;

import java.util.List;

public interface DessertService {

    DessertResponse createDessert(DessertRequest request);

    DessertResponse findDessertById(Long id);

    List<DessertResponse> findAllDesserts();

    DessertResponse updateDessert(Long id, DessertRequest request);

    void deleteDessertById(Long id);

}
