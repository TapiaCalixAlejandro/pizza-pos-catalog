package com.msvc.catalog.mapper;

import com.msvc.catalog.dto.dessert.response.DessertResponse;
import com.msvc.catalog.entity.Dessert;
import com.msvc.catalog.enums.PortionUnit;
import com.msvc.catalog.support.DessertTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DessertMapperTest {

    private final DessertMapper mapper = Mappers.getMapper(DessertMapper.class);

    @Test
    @DisplayName("Should map Request to Entity")
    void shouldMapRequestToEntity() {
        Dessert dessert = DessertTestDataBuilder
                .aDessert()
                .withId(1L)
                .build();

        DessertResponse response = mapper.toResponse(dessert);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getProductName()).isEqualTo("Pastel de Chocolate");
        assertThat(response.getPortionUnit()).isEqualTo(PortionUnit.PIECE);
    }

    @Test
    @DisplayName("Should map Entity list to Response list")
    void shouldMapEntityListToResponseList() {
        Dessert dessert = DessertTestDataBuilder.aDessert().build();
        Dessert dessert2 = DessertTestDataBuilder.aDessert().withId(2L).build();

        List<DessertResponse> responses = mapper.toResponseList(List.of(dessert, dessert2));

        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
    }

}
