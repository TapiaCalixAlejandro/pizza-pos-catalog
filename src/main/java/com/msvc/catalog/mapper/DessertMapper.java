package com.msvc.catalog.mapper;

import com.msvc.catalog.dto.dessert.response.DessertResponse;
import com.msvc.catalog.entity.Dessert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DessertMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    DessertResponse toResponse(Dessert dessert);

    List<DessertResponse> toResponseList(List<Dessert> desserts);

}
