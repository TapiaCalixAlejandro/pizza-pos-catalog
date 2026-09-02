package com.msvc.catalog.service.ingredient;

import com.msvc.catalog.dto.ingredient.request.IngredientRequest;
import com.msvc.catalog.dto.ingredient.response.IngredientResponse;

import java.util.List;

public interface IngredientService {

    List<IngredientResponse> findAllIngredient();

    IngredientResponse createIngredient(IngredientRequest request);

    IngredientResponse findByIdIngredient(Long id);

    IngredientResponse updateIngredient(Long id, IngredientRequest request);

    void deleteIngredient(Long id);

}
