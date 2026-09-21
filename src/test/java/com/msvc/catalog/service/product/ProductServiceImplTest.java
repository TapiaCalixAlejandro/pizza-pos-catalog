package com.msvc.catalog.service.product;

import com.msvc.catalog.dto.product.request.ProductRequest;
import com.msvc.catalog.dto.product.response.ProductResponse;
import com.msvc.catalog.entity.Product;
import com.msvc.catalog.enums.ProductStatus;
import com.msvc.catalog.enums.ProductType;
import com.msvc.catalog.mapper.ProductMapper;
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
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private Clock clock;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    @DisplayName("")
    void shouldCreateProductSuccessfully() {
        ProductRequest request = new ProductRequest();
        request.setName("Pizza pepperoni");
        request.setDescription("Classic pizza pepperoni");
        request.setPrice(new BigDecimal("199.99"));
        request.setImage("pepperoni.png");
        request.setProductType(ProductType.PIZZA);

        Product product = new Product();
        product.setId(1L);
        product.setName("Pizza Pepperoni");
        product.setDescription("Classic pizza pepperoni");
        product.setPrice(new BigDecimal("199.99"));
        product.setImage("pepperoni.png");
        product.setProductType(ProductType.PIZZA);
        product.setProductStatus(ProductStatus.ACTIVE);

        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setName("Pizza pepperoni");
        response.setDescription("Classic pizza pepperoni");
        response.setImage("pepperoni.png");
        response.setPrice(new BigDecimal("199.99"));
        response.setProductStatus(ProductStatus.ACTIVE);
        response.setProductType(ProductType.PIZZA);

        when(productRepository.existsByNameAndDeletedAtIsNull(request.getName()))
                .thenReturn(false);
        when(productMapper.toEntity(any(ProductRequest.class)))
                .thenReturn(product);
        when(productRepository.save(any(Product.class)))
                .thenReturn(product);
        when(productMapper.toResponse(any(Product.class)))
                .thenReturn(response);

        ProductResponse result = productService.createProduct(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Pizza pepperoni");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("199.99"));

        verify(productRepository).existsByNameAndDeletedAtIsNull(request.getName());
        verify(productMapper).toEntity(request);
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toResponse(product);
    }

    @Test
    @DisplayName("")
    void shouldThrowExceptionWhenProductAlreadyExists() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Pizza pepperoni");
        product.setDescription("Classic pizza pepperoni");
        product.setPrice(new BigDecimal("199.99"));
        product.setImage("pepperoni.png");
        product.setProductType(ProductType.PIZZA);
        product.setProductStatus(ProductStatus.ACTIVE);

        ProductRequest request = new ProductRequest();
        request.setName("Pizza pepperoni");
        request.setDescription("Classic pizza pepperoni");
        request.setPrice(new BigDecimal("199.99"));
        request.setImage("pepperoni.png");
        request.setProductType(ProductType.PIZZA);

        when(productRepository.existsByNameAndDeletedAtIsNull(request.getName()))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> productService.createProduct(request)
        );

        assertThat(exception.getMessage()).isEqualTo(Messages.PRODUCT_ALREADY_EXISTS);

        verify(productRepository).existsByNameAndDeletedAtIsNull(request.getName());
        verify(productMapper, never()).toEntity(any(ProductRequest.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("")
    void shouldReturnProductWhenProductExists() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Pizza pepperoni");
        product.setDescription("Classic pizza pepperoni");
        product.setPrice(new BigDecimal("199.99"));
        product.setImage("pepperoni.png");
        product.setProductType(ProductType.PIZZA);
        product.setProductStatus(ProductStatus.ACTIVE);

        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setName("Pizza pepperoni");
        response.setDescription("Classic pizza pepperoni");
        response.setImage("pepperoni.png");
        response.setPrice(new BigDecimal("199.99"));
        response.setProductStatus(ProductStatus.ACTIVE);
        response.setProductType(ProductType.PIZZA);

        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(product));
        when(productMapper.toResponse(product))
                .thenReturn(response);

        ProductResponse result = productService.getByIdProduct(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Pizza pepperoni");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("199.99"));
        assertThat(result.getProductType()).isEqualTo(ProductType.PIZZA);

        verify(productRepository).findByIdAndDeletedAtIsNull(1L);
        verify(productMapper).toResponse(product);
    }

    @Test
    @DisplayName("")
    void shouldCreateProductThrowExceptionWhenProductDoesNotExist() {
        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getByIdProduct(1L)
        );

        assertEquals(Messages.PRODUCT_NOT_FOUND, exception.getMessage());

        verify(productRepository).findByIdAndDeletedAtIsNull(1L);
    }

    @Test
    @DisplayName("")
    void shouldReturnAllProducts() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Pizza pepperoni");
        product.setDescription("Classic pizza pepperoni");
        product.setPrice(new BigDecimal("199.99"));
        product.setImage("pepperoni.png");
        product.setProductType(ProductType.PIZZA);
        product.setProductStatus(ProductStatus.ACTIVE);

        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setName("Pizza pepperoni");
        response.setDescription("Classic pizza pepperoni");
        response.setImage("pepperoni.png");
        response.setPrice(new BigDecimal("199.99"));
        response.setProductStatus(ProductStatus.ACTIVE);
        response.setProductType(ProductType.PIZZA);

        when(productRepository.findAllByDeletedAtIsNull())
                .thenReturn(List.of(product));
        when(productMapper.toResponse(product))
                .thenReturn(response);

        List<ProductResponse> result = productService.getAllProducts();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Pizza pepperoni");

        verify(productRepository).findAllByDeletedAtIsNull();
        verify(productMapper).toResponse(product);
    }

    @Test
    @DisplayName("")
    void shouldReturnEmptyListWhenThereAreNoProducts() {
        when(productRepository.findAllByDeletedAtIsNull())
                .thenReturn(List.of());

        List<ProductResponse> result = productService.getAllProducts();

        assertNotNull(result);
        assertThat(result).isEmpty();

        verify(productRepository).findAllByDeletedAtIsNull();
    }

    @Test
    @DisplayName("")
    void shouldUpdateProductSuccessfully() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Pizza pepperoni");
        product.setDescription("Classic pizza pepperoni");
        product.setPrice(new BigDecimal("199.99"));
        product.setImage("pepperoni.png");
        product.setProductType(ProductType.PIZZA);
        product.setProductStatus(ProductStatus.ACTIVE);

        ProductRequest request = new ProductRequest();
        request.setName("Pizza mexicana");
        request.setPrice(new BigDecimal("199.99"));
        request.setImage("mexicana.png");
        request.setDescription("Classic pizza mexicana");
        request.setProductType(ProductType.PIZZA);

        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setName("Pizza mexicana");
        response.setDescription("Classic pizza mexicana");
        response.setPrice(new BigDecimal("199.99"));
        response.setImage("mexicana.png");
        response.setProductType(ProductType.PIZZA);
        response.setProductStatus(ProductStatus.ACTIVE);

        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(product));
        when(productRepository.existsByNameAndDeletedAtIsNull(request.getName()))
                .thenReturn(false);
        when(productRepository.save(any(Product.class)))
                .thenReturn(product);
        when(productMapper.toResponse(any(Product.class)))
                .thenReturn(response);

        ProductResponse result = productService.updateProduct(1L, request);

        assertThat(result).isNotNull();
        assertThat(1L).isEqualTo(result.getId());
        assertThat("Pizza mexicana").isEqualTo(result.getName());
        assertThat(new BigDecimal("199.99")).isEqualTo(result.getPrice());

        verify(productRepository).findByIdAndDeletedAtIsNull(1L);
        verify(productRepository).existsByNameAndDeletedAtIsNull(request.getName());
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toResponse(any(Product.class));
    }

    @Test
    @DisplayName("")
    void shouldThrowExceptionWhenProductDoesNotExist() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Pizza pepperoni");
        product.setDescription("Classic pizza pepperoni");
        product.setPrice(new BigDecimal("199.99"));
        product.setImage("pepperoni.png");
        product.setProductType(ProductType.PIZZA);
        product.setProductStatus(ProductStatus.ACTIVE);

        ProductRequest request = new ProductRequest();
        request.setName("Pizza mexicana");
        request.setPrice(new BigDecimal("199.99"));
        request.setImage("mexicana.png");
        request.setDescription("Classic pizza mexicana");
        request.setProductType(ProductType.PIZZA);

        when(productRepository.findByIdAndDeletedAtIsNull(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.updateProduct(999L, request)
        );

        assertThat(Messages.PRODUCT_NOT_FOUND).isEqualTo(exception.getMessage());

        verify(productRepository).findByIdAndDeletedAtIsNull(999L);
        verify(productRepository, never()).existsByNameAndDeletedAtIsNull(anyString());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("")
    void shouldThrowExceptionWhenProductNameAlreadyExists() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Pizza pepperoni");
        product.setDescription("Classic pizza pepperoni");
        product.setPrice(new BigDecimal("199.99"));
        product.setImage("pepperoni.png");
        product.setProductType(ProductType.PIZZA);
        product.setProductStatus(ProductStatus.ACTIVE);

        ProductRequest request = new ProductRequest();
        request.setName("Pizza mexicana");
        request.setPrice(new BigDecimal("199.99"));
        request.setImage("pepperoni.png");
        request.setDescription("Classic pizza pepperoni");
        request.setProductType(ProductType.PIZZA);

        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(product));
        when(productRepository.existsByNameAndDeletedAtIsNull(request.getName()))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> productService.updateProduct(1L, request)
        );

        assertEquals(Messages.PRODUCT_ALREADY_EXISTS, exception.getMessage());

        verify(productRepository).findByIdAndDeletedAtIsNull(1L);
        verify(productRepository).existsByNameAndDeletedAtIsNull(request.getName());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("")
    void shouldDeleteProductSuccessfully() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Pizza pepperoni");
        product.setDescription("Classic pizza pepperoni");
        product.setPrice(new BigDecimal("199.99"));
        product.setImage("pepperoni.png");
        product.setProductType(ProductType.PIZZA);
        product.setProductStatus(ProductStatus.ACTIVE);

        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-09-21T18:00:00Z"),
                ZoneId.of("America/Mexico_City")
        );

        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.of(product));
        when(clock.getZone()).thenReturn(fixedClock.getZone());
        when(clock.instant()).thenReturn(fixedClock.instant());

        productService.deleteProduct(1L);

        assertNotNull(product.getDeletedAt());

        verify(productRepository).findByIdAndDeletedAtIsNull(1L);
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("")
    void shouldDeleteProductThrowExceptionWhenProductDoesNotExist() {
        when(productRepository.findByIdAndDeletedAtIsNull(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.deleteProduct(1L)
        );

        assertEquals(Messages.PRODUCT_NOT_FOUND, exception.getMessage());

        verify(productRepository).findByIdAndDeletedAtIsNull(1L);
        verify(productRepository, never()).save(any());
    }

}
