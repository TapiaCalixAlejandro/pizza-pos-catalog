package com.msvc.catalog.mapper;

import com.msvc.catalog.dto.product.request.ProductRequest;
import com.msvc.catalog.dto.product.response.ProductResponse;
import com.msvc.catalog.entity.Product;
import com.msvc.catalog.enums.ProductStatus;
import com.msvc.catalog.support.ProductTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductMapperTest {

    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    @DisplayName("Should map ProductRequest to Product entity")
    void shouldMapRequestToEntity() {
        ProductRequest request = ProductTestDataBuilder.aProduct().buildRequest();

        Product entity = mapper.toEntity(request);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("Pizza Pepperoni");
        assertThat(entity.getProductStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should map Product entity to ProductResponse")
    void shouldMapEntityToResponse() {
        Product entity = ProductTestDataBuilder.aProduct().withId(1L).build();

        ProductResponse response = mapper.toResponse(entity);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Pizza Pepperoni");
    }

    @Test
    @DisplayName("Should map list of entities to list of responses")
    void shouldMapListToResponseList() {
        Product p1 = ProductTestDataBuilder.aProduct().withId(1L).build();
        Product p2 = ProductTestDataBuilder.aProduct().withId(2L).build();

        List<ProductResponse> responses = mapper.toResponseList(List.of(p1, p2));

        assertThat(responses).hasSize(2);
    }
}