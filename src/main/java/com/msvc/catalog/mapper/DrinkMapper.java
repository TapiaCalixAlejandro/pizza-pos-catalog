package com.msvc.catalog.mapper;

import com.msvc.catalog.dto.drink.response.DrinkResponse;
import com.msvc.catalog.entity.Drink;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DrinkMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    DrinkResponse toResponse(Drink drink);

    List<DrinkResponse> toResponseList(List<Drink> drinks);

}
