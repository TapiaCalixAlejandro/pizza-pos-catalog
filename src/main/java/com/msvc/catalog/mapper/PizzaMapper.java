package com.msvc.catalog.mapper;

import com.msvc.catalog.dto.pizza.response.PizzaResponse;
import com.msvc.catalog.entity.Pizza;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = PizzaIngredientMapper.class
)
public interface PizzaMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    PizzaResponse toResponse(Pizza pizza);

    List<PizzaResponse> toResponseList(List<Pizza> pizzas);

}
