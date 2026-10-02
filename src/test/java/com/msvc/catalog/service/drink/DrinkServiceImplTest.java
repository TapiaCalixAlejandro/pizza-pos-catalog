package com.msvc.catalog.service.drink;

import com.msvc.catalog.dto.drink.request.DrinkRequest;
import com.msvc.catalog.dto.drink.response.DrinkResponse;
import com.msvc.catalog.entity.Drink;
import com.msvc.catalog.entity.Product;
import com.msvc.catalog.enums.ProductType;
import com.msvc.catalog.mapper.DrinkMapper;
import com.msvc.catalog.repository.DrinkRepository;
import com.msvc.catalog.repository.ProductRepository;
import com.msvc.catalog.shared.constans.Messages;
import com.msvc.catalog.shared.exception.BusinessException;
import com.msvc.catalog.shared.exception.ConflictException;
import com.msvc.catalog.shared.exception.ResourceNotFoundException;
import com.msvc.catalog.support.DrinkTestDataBuilder;
import com.msvc.catalog.support.ProductTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DrinkServiceImplTest {

    @Mock
    private DrinkMapper drinkMapper;

    @Mock
    private DrinkRepository drinkRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DrinkServiceImpl drinkService;

    // ============================================================
    // CREATE
    // ============================================================

    @Test
    @DisplayName("Should create drink successfully")
    void shouldCreateDrinkSuccessfully() {
        // Given
        Product product = ProductTestDataBuilder
                .aProduct()
                .withId(1L)
                .withProductType(ProductType.DRINK)
                .build();

        DrinkRequest request = DrinkTestDataBuilder
                .aDrink()
                .withProduct(product)
                .buildRequest();

        Drink savedDrink = DrinkTestDataBuilder
                .aDrink()
                .withId(10L)
                .withProduct(product)
                .build();

        DrinkResponse expectedResponse = DrinkTestDataBuilder
                .aDrink()
                .withId(10L)
                .buildResponse();

        when(productRepository.findByIdAndDeletedAtIsNull(product.getId()))
                .thenReturn(Optional.of(product));
        when(drinkRepository.existsByProductIdAndDeletedAtIsNull(product.getId()))
                .thenReturn(false);
        when(drinkRepository.save(any(Drink.class)))
                .thenReturn(savedDrink);
        when(drinkMapper.toResponse(savedDrink))
                .thenReturn(expectedResponse);

        // When
        DrinkResponse result = drinkService.createDrink(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);

        verify(productRepository).findByIdAndDeletedAtIsNull(product.getId());
        verify(drinkRepository).existsByProductIdAndDeletedAtIsNull(product.getId());
        verify(drinkRepository).save(any(Drink.class));
        verify(drinkMapper).toResponse(savedDrink);
    }

    @Test
    @DisplayName("Should throw exception when product not found during create")
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        DrinkRequest request = DrinkTestDataBuilder.aDrink().buildRequest();
        request.setProductId(999L);

        when(productRepository.findByIdAndDeletedAtIsNull(999L))
                .thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> drinkService.createDrink(request)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.PRODUCT_NOT_FOUND);

        verify(productRepository).findByIdAndDeletedAtIsNull(999L);
        verify(drinkRepository, never()).existsByProductIdAndDeletedAtIsNull(anyLong());
        verify(drinkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when product is not a drink")
    void shouldThrowExceptionWhenProductIsNotDrink() {
        // Given
        Product pizzaProduct = ProductTestDataBuilder
                .aProduct()
                .withId(1L)
                .withProductType(ProductType.PIZZA)
                .build();

        DrinkRequest request = DrinkTestDataBuilder.aDrink().buildRequest();
        request.setProductId(1L);

        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(pizzaProduct));

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> drinkService.createDrink(request)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.PRODUCT_MUST_BE_DRINK);

        verify(drinkRepository, never()).existsByProductIdAndDeletedAtIsNull(anyLong());
        verify(drinkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ConflictException when drink already exists")
    void shouldThrowExceptionWhenDrinkAlreadyExists() {
        // Given
        Product product = ProductTestDataBuilder
                .aProduct()
                .withId(1L)
                .withProductType(ProductType.DRINK)
                .build();

        DrinkRequest request = DrinkTestDataBuilder.aDrink().buildRequest();
        request.setProductId(1L);

        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(product));
        when(drinkRepository.existsByProductIdAndDeletedAtIsNull(1L))
                .thenReturn(true);

        // When & Then
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> drinkService.createDrink(request)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.DRINK_ALREADY_EXISTS);

        verify(drinkRepository, never()).save(any());
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    @Test
    @DisplayName("Should return drink by id successfully")
    void shouldReturnDrinkByIdSuccessfully() {
        // Given
        Drink drink = DrinkTestDataBuilder
                .aDrink()
                .withId(1L)
                .build();

        DrinkResponse response = DrinkTestDataBuilder
                .aDrink()
                .withId(1L)
                .buildResponse();

        when(drinkRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(drink));
        when(drinkMapper.toResponse(drink))
                .thenReturn(response);

        // When
        DrinkResponse result = drinkService.getDrinkById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(drinkRepository).findByIdAndDeletedAtIsNull(1L);
        verify(drinkMapper).toResponse(drink);
    }

    @Test
    @DisplayName("Should throw exception when drink not found by id")
    void shouldThrowExceptionWhenDrinkNotFoundById() {
        // Given
        when(drinkRepository.findByIdAndDeletedAtIsNull(999L))
                .thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> drinkService.getDrinkById(999L)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.DRINK_NOT_FOUND);

        verify(drinkRepository).findByIdAndDeletedAtIsNull(999L);
        verify(drinkMapper, never()).toResponse(any());
    }

    // ============================================================
    // GET ALL
    // ============================================================

    @Test
    @DisplayName("Should return all drinks successfully")
    void shouldReturnAllDrinksSuccessfully() {
        // Given
        Drink drink1 = DrinkTestDataBuilder.aDrink().withId(1L).build();
        Drink drink2 = DrinkTestDataBuilder.aDrink().withId(2L).build();

        DrinkResponse response1 = DrinkTestDataBuilder.aDrink().withId(1L).buildResponse();
        DrinkResponse response2 = DrinkTestDataBuilder.aDrink().withId(2L).buildResponse();

        when(drinkRepository.findAllByDeletedAtIsNull())
                .thenReturn(List.of(drink1, drink2));
        when(drinkMapper.toResponseList(List.of(drink1, drink2)))
                .thenReturn(List.of(response1, response2));

        // When
        List<DrinkResponse> result = drinkService.getAllDrinks();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);

        verify(drinkRepository).findAllByDeletedAtIsNull();
        verify(drinkMapper).toResponseList(List.of(drink1, drink2));
    }

    @Test
    @DisplayName("Should return empty list when no drinks exist")
    void shouldReturnEmptyListWhenNoDrinksExist() {
        // Given
        when(drinkRepository.findAllByDeletedAtIsNull()).thenReturn(List.of());
        when(drinkMapper.toResponseList(List.of())).thenReturn(List.of());

        // When
        List<DrinkResponse> result = drinkService.getAllDrinks();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(drinkRepository).findAllByDeletedAtIsNull();
        verify(drinkMapper).toResponseList(List.of());
    }

    // ============================================================
    // UPDATE
    // ============================================================

    @Test
    @DisplayName("Should update drink successfully")
    void shouldUpdateDrinkSuccessfully() {
        // Given
        Product currentProduct = ProductTestDataBuilder
                .aProduct()
                .withId(1L)
                .withProductType(ProductType.DRINK)
                .build();

        Product newProduct = ProductTestDataBuilder
                .aProduct()
                .withId(2L)
                .withProductType(ProductType.DRINK)
                .build();

        Drink drink = DrinkTestDataBuilder
                .aDrink()
                .withId(1L)
                .withProduct(currentProduct)
                .build();

        DrinkRequest request = DrinkTestDataBuilder
                .aDrink()
                .withProduct(newProduct)
                .buildRequest();

        DrinkResponse response = DrinkTestDataBuilder
                .aDrink()
                .withId(1L)
                .buildResponse();

        when(drinkRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(drink));
        when(productRepository.findByIdAndDeletedAtIsNull(2L))
                .thenReturn(Optional.of(newProduct));
        when(drinkRepository.existsByProductIdAndDeletedAtIsNull(2L))
                .thenReturn(false);
        when(drinkRepository.save(any(Drink.class)))
                .thenReturn(drink);
        when(drinkMapper.toResponse(drink))
                .thenReturn(response);

        // When
        DrinkResponse result = drinkService.updateDrink(1L, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        assertThat(drink.getProduct()).isEqualTo(newProduct);
        assertThat(drink.getVolume()).isEqualTo(request.getVolume());

        verify(drinkRepository).findByIdAndDeletedAtIsNull(1L);
        verify(productRepository).findByIdAndDeletedAtIsNull(2L);
        verify(drinkRepository).existsByProductIdAndDeletedAtIsNull(2L);
        verify(drinkRepository).save(any(Drink.class));
        verify(drinkMapper).toResponse(drink);
    }

    @Test
    @DisplayName("Should throw exception when drink not found during update")
    void shouldThrowExceptionWhenDrinkNotFoundDuringUpdate() {
        // Given
        DrinkRequest request = DrinkTestDataBuilder.aDrink().buildRequest();

        when(drinkRepository.findByIdAndDeletedAtIsNull(999L))
                .thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> drinkService.updateDrink(999L, request)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.DRINK_NOT_FOUND);

        verify(drinkRepository).findByIdAndDeletedAtIsNull(999L);
        verify(productRepository, never()).findByIdAndDeletedAtIsNull(anyLong());
        verify(drinkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when product not found during update")
    void shouldThrowExceptionWhenProductNotFoundDuringUpdate() {
        // Given
        Drink drink = DrinkTestDataBuilder.aDrink().withId(1L).build();

        DrinkRequest request = DrinkTestDataBuilder.aDrink().buildRequest();
        request.setProductId(999L);

        when(drinkRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(drink));
        when(productRepository.findByIdAndDeletedAtIsNull(999L))
                .thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> drinkService.updateDrink(1L, request)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.PRODUCT_NOT_FOUND);

        verify(drinkRepository).findByIdAndDeletedAtIsNull(1L);
        verify(productRepository).findByIdAndDeletedAtIsNull(999L);
        verify(drinkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when product is not a drink during update")
    void shouldThrowExceptionWhenProductIsNotDrinkDuringUpdate() {
        // Given
        Product pizzaProduct = ProductTestDataBuilder
                .aProduct()
                .withId(1L)
                .withProductType(ProductType.PIZZA)
                .build();

        Drink drink = DrinkTestDataBuilder.aDrink().withId(1L).build();

        DrinkRequest request = DrinkTestDataBuilder.aDrink().buildRequest();
        request.setProductId(1L);

        when(drinkRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(drink));
        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(pizzaProduct));

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> drinkService.updateDrink(1L, request)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.PRODUCT_MUST_BE_DRINK);

        verify(drinkRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ConflictException when new product already has a drink")
    void shouldThrowExceptionWhenNewProductAlreadyHasDrink() {
        // Given
        Product currentProduct = ProductTestDataBuilder
                .aProduct()
                .withId(1L)
                .withProductType(ProductType.DRINK)
                .build();

        Product newProduct = ProductTestDataBuilder
                .aProduct()
                .withId(2L)
                .withProductType(ProductType.DRINK)
                .build();

        Drink drink = DrinkTestDataBuilder
                .aDrink()
                .withId(1L)
                .withProduct(currentProduct)
                .build();

        DrinkRequest request = DrinkTestDataBuilder
                .aDrink()
                .withProduct(newProduct)
                .buildRequest();

        when(drinkRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(drink));
        when(productRepository.findByIdAndDeletedAtIsNull(2L))
                .thenReturn(Optional.of(newProduct));
        when(drinkRepository.existsByProductIdAndDeletedAtIsNull(2L))
                .thenReturn(true);

        // When & Then
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> drinkService.updateDrink(1L, request)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.DRINK_ALREADY_EXISTS);

        verify(drinkRepository, never()).save(any());
    }

    // ============================================================
    // DELETE
    // ============================================================

    @Test
    @DisplayName("Should soft delete drink successfully")
    void shouldDeleteDrinkSuccessfully() {
        // Given
        Drink drink = DrinkTestDataBuilder
                .aDrink()
                .withId(1L)
                .build();

        when(drinkRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(drink));

        // When
        drinkService.deleteDrinkById(1L);

        // Then
        assertThat(drink.getDeletedAt()).isNotNull();

        verify(drinkRepository).findByIdAndDeletedAtIsNull(1L);
        verify(drinkRepository).save(drink);
    }

    @Test
    @DisplayName("Should throw exception when drink not found during delete")
    void shouldThrowExceptionWhenDrinkNotFoundDuringDelete() {
        // Given
        when(drinkRepository.findByIdAndDeletedAtIsNull(999L))
                .thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> drinkService.deleteDrinkById(999L)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.DRINK_NOT_FOUND);

        verify(drinkRepository).findByIdAndDeletedAtIsNull(999L);
        verify(drinkRepository, never()).save(any());
    }

}