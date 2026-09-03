package com.msvc.catalog.mapper;

import com.msvc.catalog.dto.ingredient.request.IngredientRequest;
import com.msvc.catalog.dto.ingredient.response.IngredientResponse;
import com.msvc.catalog.entity.Ingredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IngredientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Ingredient toEntity(IngredientRequest request);

    IngredientResponse toResponse(Ingredient ingredient);

}
