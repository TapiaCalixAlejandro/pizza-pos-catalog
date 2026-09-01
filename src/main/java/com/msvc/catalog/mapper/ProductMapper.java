package com.msvc.catalog.mapper;

import com.msvc.catalog.dto.product.request.ProductRequest;
import com.msvc.catalog.dto.product.response.ProductResponse;
import com.msvc.catalog.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductRequest request);

    ProductResponse toResponse(Product product);

}
