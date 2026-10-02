package com.msvc.catalog.service.pizza;

import com.msvc.catalog.dto.pizza.request.PizzaIngredientRequest;
import com.msvc.catalog.dto.pizza.request.PizzaRequest;
import com.msvc.catalog.dto.pizza.response.PizzaResponse;
import com.msvc.catalog.entity.Ingredient;
import com.msvc.catalog.entity.Pizza;
import com.msvc.catalog.entity.PizzaIngredient;
import com.msvc.catalog.entity.Product;
import com.msvc.catalog.enums.ProductType;
import com.msvc.catalog.mapper.PizzaIngredientMapper;
import com.msvc.catalog.mapper.PizzaMapper;
import com.msvc.catalog.repository.IngredientRepository;
import com.msvc.catalog.repository.PizzaRepository;
import com.msvc.catalog.repository.ProductRepository;
import com.msvc.catalog.shared.constans.Messages;
import com.msvc.catalog.shared.exception.BusinessException;
import com.msvc.catalog.shared.exception.ConflictException;
import com.msvc.catalog.shared.exception.ResourceNotFoundException;
import com.msvc.catalog.support.IngredientTestDataBuilder;
import com.msvc.catalog.support.PizzaIngredientTestDataBuilder;
import com.msvc.catalog.support.PizzaTestDataBuilder;
import com.msvc.catalog.support.ProductTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PizzaServiceImplTest {

    @Mock
    private PizzaMapper pizzaMapper;

    @Mock
    private PizzaIngredientMapper pizzaIngredientMapper;

    @Mock
    private PizzaRepository pizzaRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private PizzaServiceImpl pizzaService;

    // ============================================================
    // CREATE
    // ============================================================

    @Test
    @DisplayName("Should create pizza successfully")
    void shouldCreatePizzaSuccessfully() {
        Product product = ProductTestDataBuilder
                .aProduct()
                .withId(1L)
                .withProductType(ProductType.PIZZA)
                .build();

        PizzaRequest request = PizzaTestDataBuilder
                .aPizza()
                .withProduct(product)
                .withPreparationTime(20)
                .buildRequest();

        Ingredient mozzarella = IngredientTestDataBuilder
                .anIngredient()
                .withId(1L)
                .build();

        Pizza savedPizza = PizzaTestDataBuilder
                .aPizza()
                .withId(10L)
                .withProduct(product)
                .withPreparationTime(20)
                .build();

        PizzaResponse expectedResponse = PizzaTestDataBuilder
                .aPizza()
                .withId(10L)
                .buildResponse();

        when(pizzaIngredientMapper.toEntity(any(PizzaIngredientRequest.class)))
                .thenAnswer(invocation -> {
                    PizzaIngredientRequest req = invocation.getArgument(0);
                    PizzaIngredient entity = new PizzaIngredient();
                    entity.setQuantity(req.getQuantity());
                    return entity;
                });
        when(productRepository.findByIdAndDeletedAtIsNull(product.getId()))
                .thenReturn(Optional.of(product));
        when(pizzaRepository.existsByProductIdAndDeletedAtIsNull(product.getId()))
                .thenReturn(false);
        when(ingredientRepository.findAllByIdInAndDeletedAtIsNull(anyList()))
                .thenReturn(List.of(mozzarella));
        when(pizzaRepository.save(any(Pizza.class)))
                .thenReturn(savedPizza);
        when(pizzaMapper.toResponse(savedPizza))
                .thenReturn(expectedResponse);

        PizzaResponse result = pizzaService.createPizza(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);

        verify(productRepository).findByIdAndDeletedAtIsNull(product.getId());
        verify(pizzaRepository).existsByProductIdAndDeletedAtIsNull(product.getId());
        verify(pizzaIngredientMapper).toEntity(any(PizzaIngredientRequest.class));
        verify(ingredientRepository).findAllByIdInAndDeletedAtIsNull(anyList());
        verify(pizzaRepository).save(any(Pizza.class));
        verify(pizzaMapper).toResponse(savedPizza);
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenProductNotFound() {
        PizzaRequest pizzaRequest = PizzaTestDataBuilder.aPizza().buildRequest();
        pizzaRequest.setProductId(1L);

        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> pizzaService.createPizza(pizzaRequest)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.PRODUCT_NOT_FOUND);

        verify(productRepository).findByIdAndDeletedAtIsNull(1L);
        verify(pizzaRepository, never()).existsByProductIdAndDeletedAtIsNull(anyLong());
        verify(pizzaIngredientMapper, never()).toEntity(any(PizzaIngredientRequest.class));
        verify(ingredientRepository, never()).findAllByIdInAndDeletedAtIsNull(anyList());
        verify(pizzaRepository, never()).save(any(Pizza.class));
        verify(pizzaMapper, never()).toResponse(any(Pizza.class));
    }

    @Test
    @DisplayName("Should throw exception when product is not a pizza")
    void shouldThrowExceptionWhenProductIsNotPizza() {
        Product dessertProduct = ProductTestDataBuilder
                .aProduct()
                .withId(1L)
                .withProductType(ProductType.DESSERT)
                .build();

        PizzaRequest request = PizzaTestDataBuilder.aPizza().buildRequest();
        request.setProductId(1L);

        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(dessertProduct));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> pizzaService.createPizza(request)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.PRODUCT_MUST_BE_PIZZA);

        verify(pizzaRepository, never()).existsByProductIdAndDeletedAtIsNull(anyLong());
        verify(pizzaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ConflictException when pizza already exists for product")
    void shouldThrowExceptionWhenPizzaAlreadyExists() {
        Product product = ProductTestDataBuilder
                .aProduct()
                .withId(1L)
                .withProductType(ProductType.PIZZA)
                .build();

        PizzaRequest request = PizzaTestDataBuilder.aPizza().buildRequest();
        request.setProductId(1L);

        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(product));
        when(pizzaRepository.existsByProductIdAndDeletedAtIsNull(1L))
                .thenReturn(true);

        // ✅ CAMBIO: ConflictException en lugar de BusinessException
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> pizzaService.createPizza(request)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.PIZZA_ALREADY_EXISTS);

        verify(ingredientRepository, never()).findAllByIdInAndDeletedAtIsNull(anyList());
        verify(pizzaRepository, never()).save(any());
        verify(pizzaMapper, never()).toResponse(any());
    }

    @Test
    @DisplayName("Should throw exception when request has duplicate ingredients")
    void shouldThrowExceptionWhenDuplicateIngredients() {
        Product product = ProductTestDataBuilder
                .aProduct()
                .withId(1L)
                .withProductType(ProductType.PIZZA)
                .build();

        PizzaRequest request = PizzaTestDataBuilder
                .aPizza()
                .withoutIngredients()  // ← Limpiamos el ingrediente por defecto
                .withPreparationTime(20)
                .addIngredient(
                        PizzaIngredientTestDataBuilder
                                .aPizzaIngredient()
                                .withIngredient(
                                        IngredientTestDataBuilder
                                                .anIngredient()
                                                .withId(1L)
                                                .build()
                                )
                                .withQuantity(new BigDecimal("0.250"))
                                .build()
                )
                .addIngredient(
                        PizzaIngredientTestDataBuilder
                                .aPizzaIngredient()
                                .withIngredient(
                                        IngredientTestDataBuilder
                                                .anIngredient()
                                                .withId(1L)  // ← duplicado
                                                .build()
                                )
                                .withQuantity(new BigDecimal("0.150"))
                                .build()
                )
                .buildRequest();
        request.setProductId(1L);

        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(product));
        when(pizzaRepository.existsByProductIdAndDeletedAtIsNull(1L))
                .thenReturn(false);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> pizzaService.createPizza(request)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.INGREDIENT_DUPLICATE);

        verify(ingredientRepository, never()).findAllByIdInAndDeletedAtIsNull(anyList());
        verify(pizzaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when some ingredients not found")
    void shouldThrowExceptionWhenSomeIngredientsNotFound() {
        Product product = ProductTestDataBuilder
                .aProduct()
                .withId(1L)
                .withProductType(ProductType.PIZZA)
                .build();

        PizzaRequest request = PizzaTestDataBuilder
                .aPizza()
                .withoutIngredients()  // ← Limpiamos
                .withPreparationTime(20)
                .addIngredient(
                        PizzaIngredientTestDataBuilder
                                .aPizzaIngredient()
                                .withIngredient(
                                        IngredientTestDataBuilder
                                                .anIngredient()
                                                .withId(1L)
                                                .build()
                                )
                                .withQuantity(new BigDecimal("0.250"))
                                .build()
                )
                .addIngredient(
                        PizzaIngredientTestDataBuilder
                                .aPizzaIngredient()
                                .withIngredient(
                                        IngredientTestDataBuilder
                                                .anIngredient()
                                                .withId(2L)
                                                .build()
                                )
                                .withQuantity(new BigDecimal("0.150"))
                                .build()
                )
                .buildRequest();
        request.setProductId(1L);

        Ingredient existing = IngredientTestDataBuilder
                .anIngredient()
                .withId(1L)
                .build();

        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(product));
        when(pizzaRepository.existsByProductIdAndDeletedAtIsNull(1L))
                .thenReturn(false);
        when(ingredientRepository.findAllByIdInAndDeletedAtIsNull(anyList()))
                .thenReturn(List.of(existing));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> pizzaService.createPizza(request)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.INGREDIENT_NOT_FOUND);

        verify(pizzaRepository, never()).save(any());
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    @Test
    @DisplayName("Should return pizza successfully")
    void shouldReturnPizzaSuccessfully() {
        Pizza pizza = PizzaTestDataBuilder
                .aPizza()
                .withId(1L)
                .build();

        PizzaResponse pizzaResponse = PizzaTestDataBuilder
                .aPizza()
                .withId(1L)
                .buildResponse();

        when(pizzaRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(pizza));
        when(pizzaMapper.toResponse(pizza))
                .thenReturn(pizzaResponse);

        PizzaResponse result = pizzaService.getPizzaById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);

        verify(pizzaRepository).findByIdAndDeletedAtIsNull(1L);
        verify(pizzaMapper).toResponse(pizza);
    }

    @Test
    @DisplayName("Should throw exception when pizza does not exists")
    void shouldThrowExceptionWhenPizzaDoesNotExists() {
        when(pizzaRepository.findByIdAndDeletedAtIsNull(2L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> pizzaService.getPizzaById(2L)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.PIZZA_NOT_FOUND);

        verify(pizzaRepository).findByIdAndDeletedAtIsNull(2L);
        verify(pizzaMapper, never()).toResponse(any());
    }

    // ============================================================
    // GET ALL
    // ============================================================

    @Test
    @DisplayName("Should return all pizzas")
    void shouldReturnAllPizzas() {
        Pizza pizza = PizzaTestDataBuilder
                .aPizza()
                .build();

        PizzaResponse response = PizzaTestDataBuilder
                .aPizza()
                .withId(1L)
                .buildResponse();

        when(pizzaRepository.findAllByDeletedAtIsNull())
                .thenReturn(List.of(pizza));
        when(pizzaMapper.toResponseList(List.of(pizza)))
                .thenReturn(List.of(response));

        List<PizzaResponse> result = pizzaService.getAllPizzas();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductName()).isEqualTo("Pizza Pepperoni");

        verify(pizzaRepository).findAllByDeletedAtIsNull();
        verify(pizzaMapper).toResponseList(List.of(pizza));
    }

    @Test
    @DisplayName("Should return empty list when there are no pizzas")
    void shouldReturnEmptyListWhenThereAreNoPizzas() {
        when(pizzaRepository.findAllByDeletedAtIsNull())
                .thenReturn(List.of());
        when(pizzaMapper.toResponseList(List.of()))
                .thenReturn(List.of());

        List<PizzaResponse> result = pizzaService.getAllPizzas();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(0);

        verify(pizzaRepository).findAllByDeletedAtIsNull();
        verify(pizzaMapper).toResponseList(List.of());
    }

    // ============================================================
    // UPDATE
    // ============================================================

    @Test
    @DisplayName("Should update pizza successfully")
    void shouldUpdatePizzaSuccessfully() {
        Product product = ProductTestDataBuilder
                .aProduct()
                .withId(2L)
                .withProductType(ProductType.PIZZA)
                .build();

        Ingredient mozzarella = IngredientTestDataBuilder
                .anIngredient()
                .withId(1L)
                .build();

        Pizza pizza = PizzaTestDataBuilder
                .aPizza()
                .withId(1L)
                .withProduct(
                        ProductTestDataBuilder
                                .aProduct()
                                .withId(1L)
                                .build()
                )
                .build();

        PizzaRequest request = PizzaTestDataBuilder
                .aPizza()
                .withProduct(product)
                .buildRequest();

        PizzaResponse response = PizzaTestDataBuilder
                .aPizza()
                .withId(1L)
                .buildResponse();

        when(pizzaRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(pizza));
        when(productRepository.findByIdAndDeletedAtIsNull(product.getId()))
                .thenReturn(Optional.of(product));
        when(pizzaRepository.existsByProductIdAndDeletedAtIsNull(product.getId()))
                .thenReturn(false);
        when(pizzaIngredientMapper.toEntity(any(PizzaIngredientRequest.class)))
                .thenAnswer(invocation -> {
                    PizzaIngredientRequest req = invocation.getArgument(0);
                    PizzaIngredient entity = new PizzaIngredient();
                    entity.setQuantity(req.getQuantity());
                    return entity;
                });
        when(ingredientRepository.findAllByIdInAndDeletedAtIsNull(anyList()))
                .thenReturn(List.of(mozzarella));
        when(pizzaRepository.save(any(Pizza.class)))
                .thenReturn(pizza);
        when(pizzaMapper.toResponse(pizza))
                .thenReturn(response);

        PizzaResponse result = pizzaService.updatePizza(1L, request);

        assertThat(result).isNotNull();
        assertThat(1L).isEqualTo(result.getId());
        assertThat("Pizza Pepperoni").isEqualTo(result.getProductName());

        assertThat(pizza.getProduct()).isEqualTo(product);
        assertThat(pizza.getPreparationTime()).isEqualTo(request.getPreparationTime());
        assertThat(pizza.getIngredients()).hasSize(1);
        assertThat(pizza.getIngredients().get(0).getIngredient()).isEqualTo(mozzarella);

        verify(pizzaRepository).findByIdAndDeletedAtIsNull(1L);
        verify(productRepository).findByIdAndDeletedAtIsNull(product.getId());
        verify(pizzaRepository).existsByProductIdAndDeletedAtIsNull(product.getId());
        verify(pizzaIngredientMapper).toEntity(any(PizzaIngredientRequest.class));
        verify(ingredientRepository).findAllByIdInAndDeletedAtIsNull(anyList());
        verify(pizzaRepository).save(any(Pizza.class));
        verify(pizzaMapper).toResponse(pizza);
    }

    @Test
    @DisplayName("Should throw exception when pizza does not exist")
    void shouldThrowExceptionWhenPizzaDoesNotExist() {
        PizzaRequest request = PizzaTestDataBuilder
                .aPizza()
                .buildRequest();

        when(pizzaRepository.findByIdAndDeletedAtIsNull(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> pizzaService.updatePizza(999L, request)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.PIZZA_NOT_FOUND);

        verify(pizzaRepository).findByIdAndDeletedAtIsNull(999L);
    }

    @Test
    @DisplayName("Should update pizza fail when product not exists")
    void shouldUpdatePizzaFailWhenProductNotExists() {
        Pizza pizza = PizzaTestDataBuilder
                .aPizza()
                .withId(1L)
                .build();

        PizzaRequest request = PizzaTestDataBuilder
                .aPizza()
                .buildRequest();
        request.setProductId(1L);

        when(pizzaRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(pizza));
        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> pizzaService.updatePizza(1L, request)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.PRODUCT_NOT_FOUND);

        verify(pizzaRepository).findByIdAndDeletedAtIsNull(1L);
        verify(productRepository).findByIdAndDeletedAtIsNull(1L);
    }

    @Test
    @DisplayName("Should update pizza fail when product is not pizza")
    void shouldUpdatePizzaFailWhenProductIsNotPizza() {
        Product product = ProductTestDataBuilder
                .aProduct()
                .withId(1L)
                .withProductType(ProductType.DESSERT)
                .build();

        Pizza pizza = PizzaTestDataBuilder
                .aPizza()
                .withId(1L)
                .build();

        PizzaRequest request = PizzaTestDataBuilder
                .aPizza()
                .buildRequest();
        request.setProductId(1L);

        when(pizzaRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(pizza));
        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(product));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> pizzaService.updatePizza(1L, request)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.PRODUCT_MUST_BE_PIZZA);

        verify(pizzaRepository).findByIdAndDeletedAtIsNull(1L);
        verify(productRepository).findByIdAndDeletedAtIsNull(1L);
    }

    @Test
    @DisplayName("Should update pizza fail when new product already has pizza")
    void shouldUpdatePizzaFailWhenNewProductAlreadyHasPizza() {
        Product currentProduct = ProductTestDataBuilder
                .aProduct()
                .withId(1L)
                .withProductType(ProductType.PIZZA)
                .build();

        Product newProduct = ProductTestDataBuilder
                .aProduct()
                .withId(2L)
                .withProductType(ProductType.PIZZA)
                .build();

        Pizza pizza = PizzaTestDataBuilder
                .aPizza()
                .withId(1L)
                .withProduct(currentProduct)
                .build();

        PizzaRequest request = PizzaTestDataBuilder
                .aPizza()
                .withProduct(newProduct)
                .buildRequest();

        when(pizzaRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(pizza));
        when(productRepository.findByIdAndDeletedAtIsNull(2L))
                .thenReturn(Optional.of(newProduct));
        when(pizzaRepository.existsByProductIdAndDeletedAtIsNull(2L))
                .thenReturn(true);

        // ✅ CAMBIO: ConflictException
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> pizzaService.updatePizza(1L, request)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.PIZZA_ALREADY_EXISTS);

        verify(pizzaRepository).findByIdAndDeletedAtIsNull(1L);
        verify(productRepository).findByIdAndDeletedAtIsNull(2L);
        verify(pizzaRepository).existsByProductIdAndDeletedAtIsNull(2L);
    }

    @Test
    @DisplayName("Should update pizza throw exception when request has duplicate ingredients")
    void shouldUpdatePizzaThrowExceptionWhenDuplicateIngredients() {
        Product product = ProductTestDataBuilder
                .aProduct()
                .withId(1L)
                .withProductType(ProductType.PIZZA)
                .build();

        Pizza pizza = PizzaTestDataBuilder
                .aPizza()
                .withId(1L)
                .withProduct(product)
                .build();

        PizzaRequest request = PizzaTestDataBuilder
                .aPizza()
                .withProduct(product)
                .withoutIngredients()  // ← Limpiamos
                .withPreparationTime(20)
                .addIngredient(
                        PizzaIngredientTestDataBuilder
                                .aPizzaIngredient()
                                .withIngredient(
                                        IngredientTestDataBuilder
                                                .anIngredient()
                                                .withId(1L)
                                                .build()
                                )
                                .withQuantity(new BigDecimal("0.250"))
                                .build()
                )
                .addIngredient(
                        PizzaIngredientTestDataBuilder
                                .aPizzaIngredient()
                                .withIngredient(
                                        IngredientTestDataBuilder
                                                .anIngredient()
                                                .withId(1L)
                                                .build()
                                )
                                .withQuantity(new BigDecimal("0.150"))
                                .build()
                )
                .buildRequest();
        request.setProductId(1L);

        when(pizzaRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(pizza));
        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(product));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> pizzaService.updatePizza(1L, request)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.INGREDIENT_DUPLICATE);

        verify(pizzaRepository).findByIdAndDeletedAtIsNull(1L);
        verify(productRepository).findByIdAndDeletedAtIsNull(1L);
        verify(ingredientRepository, never()).findAllByIdInAndDeletedAtIsNull(anyList());
        verify(pizzaRepository, never()).save(any());
    }

    // ============================================================
    // DELETE
    // ============================================================

    @Test
    @DisplayName("Should delete pizza successfully when pizza exists")
    void shouldDeletePizzaSuccessfullyWhenPizzaExists() {
        Pizza pizza = PizzaTestDataBuilder
                .aPizza()
                .withId(1L)
                .build();

        when(pizzaRepository.findByIdAndDeletedAtIsNull(pizza.getId()))
                .thenReturn(Optional.of(pizza));

        pizzaService.deletePizzaById(pizza.getId());

        assertThat(pizza.getDeletedAt()).isNotNull();
        assertThat(pizza.getIngredients())
                .allSatisfy(
                        ingredient -> assertThat(ingredient.getDeletedAt()).isNotNull()
                );

        verify(pizzaRepository).findByIdAndDeletedAtIsNull(pizza.getId());
        verify(pizzaRepository).save(pizza);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when pizza does not exist during delete")
    void shouldThrowExceptionWhenIngredientDoesNotExist() {
        when(pizzaRepository.findByIdAndDeletedAtIsNull(9L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> pizzaService.deletePizzaById(9L)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.PIZZA_NOT_FOUND);

        verify(pizzaRepository).findByIdAndDeletedAtIsNull(9L);
        verify(pizzaRepository, never()).save(any());
    }
}