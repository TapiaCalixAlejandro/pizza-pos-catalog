package com.msvc.catalog.service.dessert;

import com.msvc.catalog.dto.dessert.request.DessertRequest;
import com.msvc.catalog.dto.dessert.response.DessertResponse;
import com.msvc.catalog.entity.Dessert;
import com.msvc.catalog.entity.Product;
import com.msvc.catalog.enums.PortionUnit;
import com.msvc.catalog.enums.ProductStatus;
import com.msvc.catalog.enums.ProductType;
import com.msvc.catalog.mapper.DessertMapper;
import com.msvc.catalog.repository.DessertRepository;
import com.msvc.catalog.repository.ProductRepository;
import com.msvc.catalog.shared.constans.Messages;
import com.msvc.catalog.shared.exception.BusinessException;
import com.msvc.catalog.shared.exception.ResourceNotFoundException;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class DessertServiceImplTest {

    @Mock
    private DessertMapper dessertMapper;

    @Mock
    private DessertRepository dessertRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DessertServiceImpl dessertService;

    @Test
    @DisplayName("Should create dessert successfully")
    void shouldCreateDessertSuccessfully() {
        // Given -> Datos
        DessertRequest request = new DessertRequest();
        request.setProductId(1L);
        request.setPortion(1);
        request.setPortionUnit(PortionUnit.PIECE);

        Product product = new Product();
        product.setId(1L);
        product.setName("Pastel de chocolate");
        product.setDescription("Rebanada de pastel de chocolate");
        product.setPrice(new BigDecimal("20.00"));
        product.setProductType(ProductType.DESSERT);
        product.setProductStatus(ProductStatus.ACTIVE);
        product.setImage("pastel-chocolate.png");

        Dessert dessert = new Dessert();
        dessert.setId(1L);
        dessert.setProduct(product);
        dessert.setPortion(1);
        dessert.setPortionUnit(PortionUnit.PIECE);

        DessertResponse response = new DessertResponse();
        response.setId(1L);
        response.setProductId(1L);
        response.setProductName("Pastel de chocolate");
        response.setPortion(1);
        response.setPortionUnit(PortionUnit.PIECE);

        // Configuracion de mocks -> When
        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(product));
        when(dessertRepository.existsByProductIdAndDeletedAtIsNull(1L))
                .thenReturn(false);
        when(dessertRepository.save(any(Dessert.class)))
                .thenReturn(dessert);
        when(dessertMapper.toResponse(dessert))
                .thenReturn(response);

        DessertResponse result = dessertService.createDessert(request);

        assertNotNull(result);
        assertEquals(1L, result.getProductId());
        assertEquals(1, result.getPortion());
        assertEquals(PortionUnit.PIECE, result.getPortionUnit());

        verify(productRepository).findByIdAndDeletedAtIsNull(1L);
        verify(dessertRepository).existsByProductIdAndDeletedAtIsNull(1L);
        verify(dessertRepository).save(any(Dessert.class));
    }

    @Test
    @DisplayName("Should fail when product id not found")
    void shouldFailWhenProductIdNotFound() {
        DessertRequest request = new DessertRequest();
        request.setProductId(1L);
        request.setPortion(1);
        request.setPortionUnit(PortionUnit.PIECE);

        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> dessertService.createDessert(request)
        );

        assertEquals(Messages.PRODUCT_NOT_FOUND, exception.getMessage());

        verify(productRepository).findByIdAndDeletedAtIsNull(1L);
        verify(dessertRepository, never()).save(any(Dessert.class));
    }

    @Test
    @DisplayName("Should fail when product is not a dessert")
    void shouldFailWhenProductIsNotADessert() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Pastel de chocolate");
        product.setDescription("Rebanada de pastel de chocolate");
        product.setPrice(new BigDecimal("20.00"));
        product.setProductType(ProductType.PIZZA);
        product.setProductStatus(ProductStatus.ACTIVE);
        product.setImage("pastel-chocolate.png");

        DessertRequest request = new DessertRequest();
        request.setProductId(1L);
        request.setPortion(1);
        request.setPortionUnit(PortionUnit.PIECE);

        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(product));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> dessertService.createDessert(request)
        );

        assertEquals(Messages.PRODUCT_MUST_BE_DESSERT, exception.getMessage());

        verify(productRepository).findByIdAndDeletedAtIsNull(1L);
        verify(dessertRepository, never()).existsByProductIdAndDeletedAtIsNull(1L);
        verify(dessertRepository, never()).save(any(Dessert.class));
    }

    @Test
    @DisplayName("Should fail when dessert already exists")
    void shouldFailWhenDessertAlreadyExists() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Pastel de chocolate");
        product.setDescription("Rebanada de pastel de chocolate");
        product.setPrice(new BigDecimal("20.00"));
        product.setProductType(ProductType.DESSERT);
        product.setProductStatus(ProductStatus.ACTIVE);
        product.setImage("pastel-chocolate.png");

        DessertRequest request = new DessertRequest();
        request.setProductId(1L);
        request.setPortion(1);
        request.setPortionUnit(PortionUnit.PIECE);

        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(product));
        when(dessertRepository.existsByProductIdAndDeletedAtIsNull(1L))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> dessertService.createDessert(request)
        );

        assertEquals(Messages.DESSERT_ALREADY_EXISTS, exception.getMessage());

        verify(productRepository).findByIdAndDeletedAtIsNull(1L);
        verify(dessertRepository).existsByProductIdAndDeletedAtIsNull(1L);
        verify(dessertRepository, never()).save(any(Dessert.class));
    }

    @Test
    @DisplayName("Should find dessert by id successfully")
    void shouldFindDessertByIdSuccessfully() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Pastel de chocolate");
        product.setDescription("Rebanada de pastel de chocolate");
        product.setPrice(new BigDecimal("20.00"));
        product.setProductType(ProductType.DESSERT);
        product.setProductStatus(ProductStatus.ACTIVE);
        product.setImage("pastel-chocolate.png");

        Dessert dessert = new Dessert();
        dessert.setId(1L);
        dessert.setProduct(product);
        dessert.setPortion(1);
        dessert.setPortionUnit(PortionUnit.PIECE);

        DessertResponse response = new DessertResponse();
        response.setId(1L);
        response.setProductId(1L);
        response.setProductName("Pastel de chocolate");
        response.setPortion(1);
        response.setPortionUnit(PortionUnit.PIECE);

        when(dessertRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(dessert));
        when(dessertMapper.toResponse(dessert))
                .thenReturn(response);

        DessertResponse result = dessertService.findDessertById(1L);

        assertEquals(1L, result.getId());

        verify(dessertRepository).findByIdAndDeletedAtIsNull(1L);
    }

    @Test
    @DisplayName("Should faild find dessert by id when dessert not exists")
    void shouldFailFindDessertByIdWhenDessertNotExists() {
        when(dessertRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> dessertService.findDessertById(1L)
        );
        assertEquals(Messages.DESSERT_NOT_FOUND, exception.getMessage());

        verify(dessertRepository).findByIdAndDeletedAtIsNull(1L);
    }

    @Test
    @DisplayName("Should find all desserts successfully")
    void shouldFindAllDessertsSuccessfully() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Pastel de chocolate");
        product.setDescription("Rebanada de pastel de chocolate");
        product.setPrice(new BigDecimal("20.00"));
        product.setProductType(ProductType.DESSERT);
        product.setProductStatus(ProductStatus.ACTIVE);
        product.setImage("pastel-chocolate.png");

        Dessert dessert = new Dessert();
        dessert.setId(1L);
        dessert.setProduct(product);
        dessert.setPortion(1);
        dessert.setPortionUnit(PortionUnit.PIECE);

        DessertResponse response = new DessertResponse();
        response.setId(1L);
        response.setProductId(1L);
        response.setProductName("Pastel de chocolate");
        response.setPortion(1);
        response.setPortionUnit(PortionUnit.PIECE);

        when(dessertRepository.findAllByDeletedAtIsNull())
                .thenReturn(List.of(dessert));
        when(dessertMapper.toResponseList(List.of(dessert)))
                .thenReturn(List.of(response));

        List<DessertResponse> responses = dessertService.findAllDesserts();

        assertEquals(1, responses.size());

        verify(dessertRepository).findAllByDeletedAtIsNull();
    }

    @Test
    @DisplayName("Should update dessert successfully")
    void shouldUpdateDessertSuccessfully() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Pastel de chocolate");
        product.setDescription("Rebanada de pastel de chocolate");
        product.setPrice(new BigDecimal("20.00"));
        product.setProductType(ProductType.DESSERT);
        product.setProductStatus(ProductStatus.ACTIVE);
        product.setImage("pastel-chocolate.png");

        Dessert dessert = new Dessert();
        dessert.setId(1L);
        dessert.setProduct(product);
        dessert.setPortion(1);
        dessert.setPortionUnit(PortionUnit.PIECE);

        DessertRequest request = new DessertRequest();
        request.setProductId(1L);
        request.setPortion(1);
        request.setPortionUnit(PortionUnit.PIECE);

        DessertResponse response = new DessertResponse();
        response.setId(1L);
        response.setProductId(1L);
        response.setProductName("Pastel de chocolate");
        response.setPortion(1);
        response.setPortionUnit(PortionUnit.PIECE);

        when(dessertRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(dessert));
        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(product));
        when(dessertRepository.save(any(Dessert.class)))
                .thenReturn(dessert);
        when(dessertMapper.toResponse(dessert))
                .thenReturn(response);

        DessertResponse result = dessertService.updateDessert(1L, request);

        assertNotNull(result);
        assertEquals(1L, result.getProductId());
        assertEquals(1, result.getPortion());
        assertEquals("Pastel de chocolate", result.getProductName());
        assertEquals(PortionUnit.PIECE, result.getPortionUnit());

        verify(productRepository).findByIdAndDeletedAtIsNull(1L);
        verify(dessertRepository).findByIdAndDeletedAtIsNull(1L);
        verify(dessertRepository).save(dessert);
    }

    @Test
    @DisplayName("Should update dessert fail when dessert not exists")
    void shouldUpdateDessertFailWhenDessertNotExists() {
        DessertRequest request = new DessertRequest();
        request.setProductId(1L);
        request.setPortion(1);
        request.setPortionUnit(PortionUnit.PIECE);

        when(dessertRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> dessertService.updateDessert(1L, request)
        );

        assertEquals(Messages.DESSERT_NOT_FOUND, exception.getMessage());

        verify(dessertRepository).findByIdAndDeletedAtIsNull(1L);
        verify(dessertRepository, never()).save(any(Dessert.class));
    }

    @Test
    @DisplayName("Should update dessert fail when product not exists")
    void shouldUpdateDessertFailWhenProductNotExists() {
        DessertRequest request = new DessertRequest();
        request.setProductId(1L);
        request.setPortion(1);
        request.setPortionUnit(PortionUnit.PIECE);

        Dessert dessert = new Dessert();
        dessert.setId(1L);
        dessert.setProduct(new Product());
        dessert.setPortion(1);
        dessert.setPortionUnit(PortionUnit.PIECE);
        dessert.getProduct().setId(1L);

        when(dessertRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(dessert));
        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> dessertService.updateDessert(1L, request)
        );

        assertEquals(Messages.PRODUCT_NOT_FOUND, exception.getMessage());

        verify(dessertRepository).findByIdAndDeletedAtIsNull(1L);
        verify(productRepository).findByIdAndDeletedAtIsNull(1L);
        verify(dessertRepository, never()).save(any(Dessert.class));
    }

    @Test
    @DisplayName("Should update dessert fail when product is not dessert")
    void shouldUpdateDessertFailWhenProductIsNotDessert() {
        DessertRequest request = new DessertRequest();
        request.setProductId(1L);
        request.setPortion(1);
        request.setPortionUnit(PortionUnit.PIECE);

        Product product = new Product();
        product.setId(1L);
        product.setName("Pastel de chocolate");
        product.setDescription("Rebanada de pastel de chocolate");
        product.setPrice(new BigDecimal("20.00"));
        product.setProductType(ProductType.PIZZA);
        product.setProductStatus(ProductStatus.ACTIVE);
        product.setImage("pastel-chocolate.png");

        Dessert dessert = new Dessert();
        dessert.setId(1L);
        dessert.setProduct(product);
        dessert.setPortion(1);
        dessert.setPortionUnit(PortionUnit.PIECE);

        when(dessertRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(dessert));
        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(product));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> dessertService.updateDessert(1L, request)
        );

        assertEquals(Messages.PRODUCT_MUST_BE_DESSERT, exception.getMessage());

        verify(dessertRepository).findByIdAndDeletedAtIsNull(1L);
        verify(productRepository).findByIdAndDeletedAtIsNull(1L);
        verify(dessertRepository, never()).save(any(Dessert.class));
    }

    @Test
    @DisplayName("Should update dessert fail when new product already has dessert")
    void shouldUpdateDessertFailWhenNewProductAlreadyHasDessert() {
        Product currentProduct = new Product();
        currentProduct.setId(1L);
        currentProduct.setProductType(ProductType.DESSERT);

        Product newProduct = new Product();
        newProduct.setId(2L);
        newProduct.setProductType(ProductType.DESSERT);

        Dessert dessert = new Dessert();
        dessert.setId(1L);
        dessert.setProduct(currentProduct);
        dessert.setPortion(1);
        dessert.setPortionUnit(PortionUnit.PIECE);

        DessertRequest request = new DessertRequest();
        request.setProductId(2L);
        request.setPortion(2);
        request.setPortionUnit(PortionUnit.SLICE);

        when(dessertRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(dessert));
        when(productRepository.findByIdAndDeletedAtIsNull(2L))
                .thenReturn(Optional.of(newProduct));
        when(dessertRepository.existsByProductIdAndDeletedAtIsNull(2L))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> dessertService.updateDessert(1L, request)
        );

        assertEquals(Messages.DESSERT_ALREADY_EXISTS, exception.getMessage());

        verify(dessertRepository).findByIdAndDeletedAtIsNull(1L);
        verify(productRepository).findByIdAndDeletedAtIsNull(2L);
        verify(dessertRepository).existsByProductIdAndDeletedAtIsNull(2L);
        verify(dessertRepository, never()).save(any(Dessert.class));
    }

    @Test
    @DisplayName("Shuld soft delete by id successfully")
    void shouldSoftDeleteByIdSuccessfully() {
        Dessert dessert = new Dessert();
        dessert.setId(1L);
        dessert.setProduct(new Product());
        dessert.setPortion(1);
        dessert.setPortionUnit(PortionUnit.PIECE);
        dessert.getProduct().setId(1L);

        when(dessertRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(dessert));

        dessertService.deleteDessertById(1L);

        assertNotNull(dessert.getDeletedAt());

        verify(dessertRepository).findByIdAndDeletedAtIsNull(1L);
        verify(dessertRepository).save(dessert);
    }

    @Test
    @DisplayName("Should delete dessert fail when dessert not exists")
    void shouldDeleteDessertFailWhenDessertNotExists() {
        when(dessertRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> dessertService.deleteDessertById(1L)
        );

        assertEquals(Messages.DESSERT_NOT_FOUND, exception.getMessage());

        verify(dessertRepository).findByIdAndDeletedAtIsNull(1L);
        verify(dessertRepository, never()).save(any(Dessert.class));
    }

}
