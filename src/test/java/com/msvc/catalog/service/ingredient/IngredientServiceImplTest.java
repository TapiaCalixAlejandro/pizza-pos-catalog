package com.msvc.catalog.service.ingredient;

import com.msvc.catalog.dto.ingredient.request.IngredientRequest;
import com.msvc.catalog.dto.ingredient.response.IngredientResponse;
import com.msvc.catalog.entity.Ingredient;
import com.msvc.catalog.enums.IngredientUnit;
import com.msvc.catalog.mapper.IngredientMapper;
import com.msvc.catalog.repository.IngredientRepository;
import com.msvc.catalog.shared.constans.Messages;
import com.msvc.catalog.shared.exception.BusinessException;
import com.msvc.catalog.shared.exception.ResourceNotFoundException;
import com.msvc.catalog.support.IngredientTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class IngredientServiceImplTest {

    @Mock
    private IngredientMapper ingredientMapper;

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientServiceImpl ingredientService;

    @Test
    @DisplayName("Should return all ingredients successfully")
    void shouldReturnAllIngredientsSuccessfully() {
        Ingredient ingredient = IngredientTestDataBuilder
                .anIngredient()
                .build();

        ingredient.setId(1L);

        IngredientResponse response = IngredientTestDataBuilder
                .anIngredient()
                .buildResponse();

        when(ingredientRepository.findAllByDeletedAtIsNull())
                .thenReturn(List.of(ingredient));
        when(ingredientMapper.toResponse(ingredient))
                .thenReturn(response);

        List<IngredientResponse> result = ingredientService.findAllIngredient();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Mozzarella", result.getFirst().getName());

        verify(ingredientRepository).findAllByDeletedAtIsNull();
        verify(ingredientMapper).toResponse(ingredient);
    }

    @Test
    @DisplayName("Should return an empty list when there are no ingredients")
    void shouldReturnEmptyListWhenThereAreNoIngredients() {
        when(ingredientRepository.findAllByDeletedAtIsNull())
                .thenReturn(List.of());

        List<IngredientResponse> result = ingredientService.findAllIngredient();

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(ingredientRepository).findAllByDeletedAtIsNull();
    }

    @Test
    @DisplayName("Should create ingredient successfully")
    void shouldCreateIngredientSuccessfully() {
        IngredientRequest request = IngredientTestDataBuilder.anIngredient().buildRequest();
        Ingredient ingredient = IngredientTestDataBuilder.anIngredient().build();
        IngredientResponse response = IngredientTestDataBuilder.anIngredient().buildResponse();

        when(ingredientRepository.existsByNameAndDeletedAtIsNull(request.getName()))
                .thenReturn(false);
        when(ingredientMapper.toEntity(request))
                .thenReturn(ingredient);
        when(ingredientRepository.save(any(Ingredient.class)))
                .thenReturn(ingredient);
        when(ingredientMapper.toResponse(ingredient))
                .thenReturn(response);

        IngredientResponse result = ingredientService.createIngredient(request);

        assertNotNull(result);
        assertEquals("Mozzarella", result.getName());
        assertEquals(IngredientUnit.KILOGRAM, result.getUnit());
        assertEquals(new BigDecimal("20"), result.getStock());

        verify(ingredientRepository).existsByNameAndDeletedAtIsNull(request.getName());
        verify(ingredientMapper).toEntity(request);
        verify(ingredientRepository).save(ingredient);
        verify(ingredientMapper).toResponse(ingredient);
    }

    @Test
    @DisplayName("Should throw BusinessException when ingredient already exists")
    void shouldThrowExceptionWhenIngredientAlreadyExists() {
        IngredientRequest request = IngredientTestDataBuilder.anIngredient().buildRequest();

        when(ingredientRepository.existsByNameAndDeletedAtIsNull(request.getName()))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> ingredientService.createIngredient(request)
        );

        assertEquals(Messages.INGREDIENT_ALREADY_EXISTS, exception.getMessage());

        verify(ingredientRepository).existsByNameAndDeletedAtIsNull(request.getName());
        verify(ingredientMapper, never()).toEntity(any(IngredientRequest.class));
        verify(ingredientRepository, never()).save(any(Ingredient.class));
        verify(ingredientMapper, never()).toResponse(any(Ingredient.class));
    }

    @Test
    @DisplayName("Should return ingredient when it exists")
    void shouldReturnIngredientWhenIngredientExists() {
        Ingredient ingredient = IngredientTestDataBuilder.anIngredient().build();
        IngredientResponse response = IngredientTestDataBuilder.anIngredient().buildResponse();

        when(ingredientRepository.findByIdAndDeletedAtIsNull(ingredient.getId()))
                .thenReturn(Optional.of(ingredient));
        when(ingredientMapper.toResponse(ingredient))
                .thenReturn(response);

        IngredientResponse result = ingredientService.findByIdIngredient(ingredient.getId());

        assertNotNull(result);
        assertEquals("Mozzarella", result.getName());

        verify(ingredientRepository).findByIdAndDeletedAtIsNull(ingredient.getId());
        verify(ingredientMapper).toResponse(ingredient);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when ingredient does not exist")
    void shouldThrowExceptionWhenIngredientDoesNotExists() {
        when(ingredientRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ingredientService.findByIdIngredient(1L)
        );

        assertEquals(Messages.INGREDIENT_NOT_FOUND, exception.getMessage());

        verify(ingredientRepository).findByIdAndDeletedAtIsNull(1L);
        verify(ingredientMapper, never()).toResponse(any(Ingredient.class));
    }

    @Test
    @DisplayName("Should update ingredient successfully")
    void shouldUpdateIngredientSuccessfully() {
        Ingredient exists = IngredientTestDataBuilder.anIngredient().build();
        IngredientRequest request = IngredientTestDataBuilder
                .anIngredient()
                .withName("Mass")
                .buildRequest();

        request.setCost(new BigDecimal("190.00"));

        IngredientResponse response = IngredientTestDataBuilder
                .anIngredient()
                .withName("Mass")
                .withCost(new BigDecimal("190.00"))
                .buildResponse();

        when(ingredientRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(exists));
        when(ingredientRepository.existsByNameAndDeletedAtIsNull(request.getName()))
                .thenReturn(false);
        when(ingredientMapper.toResponse(any(Ingredient.class)))
                .thenReturn(response);

        IngredientResponse result = ingredientService.updateIngredient(1L, request);

        assertNotNull(result);
        assertEquals("Mass", result.getName());
        assertEquals(new BigDecimal("190.00"), result.getCost());

        verify(ingredientRepository).findByIdAndDeletedAtIsNull(1L);
        verify(ingredientRepository).existsByNameAndDeletedAtIsNull(request.getName());
        verify(ingredientMapper).toResponse(any(Ingredient.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when ingredient does not exist")
    void shouldThrowExceptionWhenIngredientDoesNotExist() {
        IngredientRequest request = IngredientTestDataBuilder.anIngredient().buildRequest();

        when(ingredientRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ingredientService.updateIngredient(1L, request)
        );

        assertEquals(Messages.INGREDIENT_NOT_FOUND, exception.getMessage());

        verify(ingredientRepository).findByIdAndDeletedAtIsNull(1L);
        verify(ingredientRepository, never()).existsByNameAndDeletedAtIsNull(anyString());
        verify(ingredientMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Should throw BusinessException when ingredient name already exists")
    void shouldThrowExceptionWhenIngredientNameAlreadyExist() {
        Ingredient exists = IngredientTestDataBuilder.anIngredient().build();
        exists.setId(1L);
        Ingredient another = IngredientTestDataBuilder
                .anIngredient()
                .withName("Mass")
                .build();
        another.setId(2L);
        IngredientRequest request = IngredientTestDataBuilder
                .anIngredient()
                .withName("Mass")
                .buildRequest();

        when(ingredientRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(exists));
        when(ingredientRepository.existsByNameAndDeletedAtIsNull(request.getName()))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> ingredientService.updateIngredient(1L, request)
        );

        assertEquals(Messages.INGREDIENT_ALREADY_EXISTS, exception.getMessage());

        verify(ingredientRepository).findByIdAndDeletedAtIsNull(1L);
        verify(ingredientRepository).existsByNameAndDeletedAtIsNull(request.getName());
        verify(ingredientMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Should delete ingredient successfully when ingredient exists")
    void shouldDeleteIngredientSuccessfullyWhenIngredientExists() {
        Ingredient ingredient = IngredientTestDataBuilder.anIngredient().build();

        when(ingredientRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(ingredient));

        ingredientService.deleteIngredient(1L);

        assertNotNull(ingredient.getDeletedAt());

        verify(ingredientRepository).findByIdAndDeletedAtIsNull(1L);
        verify(ingredientRepository).save(ingredient);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting ingredient that does not exist")
    void shouldSoftDeleteThrowExceptionWhenIngredientDoesNotExist() {
        when(ingredientRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ingredientService.deleteIngredient(1L)
        );

        assertEquals(Messages.INGREDIENT_NOT_FOUND, exception.getMessage());

        verify(ingredientRepository).findByIdAndDeletedAtIsNull(1L);
        verify(ingredientRepository, never()).save(any());
    }

}
