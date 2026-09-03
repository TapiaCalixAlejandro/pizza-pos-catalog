package com.msvc.catalog.mapper;

import com.msvc.catalog.dto.pizza.request.PizzaIngredientRequest;
import com.msvc.catalog.dto.pizza.response.PizzaIngredientResponse;
import com.msvc.catalog.entity.PizzaIngredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PizzaIngredientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pizza", ignore = true)
    @Mapping(target = "ingredient", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    PizzaIngredient toEntity(PizzaIngredientRequest request);

    @Mapping(target = "ingredientId", source = "ingredient.id")
    @Mapping(target = "ingredientName", source = "ingredient.name")
    PizzaIngredientResponse toResponse(PizzaIngredient entity);

}
