package com.msvc.catalog.mapper;

import com.msvc.catalog.dto.ingredient.request.IngredientRequest;
import com.msvc.catalog.dto.ingredient.response.IngredientResponse;
import com.msvc.catalog.entity.Ingredient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IngredientMapper {

    Ingredient toEntity(IngredientRequest request);

    IngredientResponse toResponse(Ingredient ingredient);

}
