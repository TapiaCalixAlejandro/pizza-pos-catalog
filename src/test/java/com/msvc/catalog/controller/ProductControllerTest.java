package com.msvc.catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msvc.catalog.dto.product.request.ProductRequest;
import com.msvc.catalog.dto.product.response.ProductResponse;
import com.msvc.catalog.enums.ProductStatus;
import com.msvc.catalog.enums.ProductType;
import com.msvc.catalog.service.product.ProductService;
import com.msvc.catalog.shared.constans.Messages;
import com.msvc.catalog.shared.exception.ConflictException;
import com.msvc.catalog.shared.exception.ResourceNotFoundException;
import com.msvc.catalog.shared.responses.ApiErrorResponse;
import com.msvc.catalog.shared.responses.ApiResponse;
import com.msvc.catalog.shared.responses.ResponseFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper  objectMapper = new ObjectMapper();

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ResponseFactory responseFactory;

    @Test
    @DisplayName("Should return 201 when product is created successfully")
    void shouldCreateProductSuccessfully() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Pizza pepperoni");
        request.setDescription("Classic pizza pepperoni");
        request.setPrice(new BigDecimal("199.99"));
        request.setImage("pepperoni.png");
        request.setProductType(ProductType.PIZZA);

        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setName("Pizza pepperoni");
        response.setDescription("Classic pizza pepperoni");
        response.setPrice(new BigDecimal("199.99"));
        response.setImage("pepperoni.png");
        response.setProductType(ProductType.PIZZA);
        response.setProductStatus(ProductStatus.ACTIVE);

        ApiResponse<ProductResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        Messages.PRODUCT_CREATED,
                        "test-trace-id",
                        response,
                        null
                );

        when(productService.createProduct(any(ProductRequest.class)))
                .thenReturn(response);
        when(responseFactory.success(eq(Messages.PRODUCT_CREATED), eq(response)))
                .thenReturn(apiResponse);

        mockMvc
                .perform(
                        post("/api/products")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.PRODUCT_CREATED))
                .andExpect(jsonPath("$.traceId").value("test-trace-id"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Pizza pepperoni"))
                .andExpect(jsonPath("$.data.description").value("Classic pizza pepperoni"))
                .andExpect(jsonPath("$.data.price").value(199.99))
                .andExpect(jsonPath("$.data.image").value("pepperoni.png"))
                .andExpect(jsonPath("$.data.productType").value("PIZZA"))
                .andExpect(jsonPath("$.data.productStatus").value("ACTIVE"));

        verify(productService).createProduct(any(ProductRequest.class));
        verify(responseFactory).success(eq(Messages.PRODUCT_CREATED), eq(response));
    }

    @Test
    @DisplayName("Should return bad request when create request is invalid")
    void shouldReturnBadRequestWhenCreateProductRequestIsInvalid() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("");
        request.setDescription("");
        request.setImage("");
        request.setPrice(new BigDecimal(0));
        request.setProductType(null);

        mockMvc
                .perform(
                        post("/api/products")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("Should return conflict when product already exists")
    void shouldReturnConflictWhenProductAlreadyExists() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Pizza pepperoni");
        request.setDescription("Classic pizza pepperoni");
        request.setPrice(new BigDecimal("199.99"));
        request.setImage("pepperoni.png");
        request.setProductType(ProductType.PIZZA);

        when(productService.createProduct(any(ProductRequest.class)))
                .thenThrow(new ConflictException(Messages.PRODUCT_ALREADY_EXISTS));

        mockMvc
                .perform(
                        post("/api/products")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict());

        verify(productService).createProduct(any(ProductRequest.class));
    }

    @Test
    @DisplayName("Should get product by id successfully")
    void shouldGetProductByIdSuccessfully() throws Exception {
        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setName("Pizza pepperoni");
        response.setDescription("Classic pizza pepperoni");
        response.setPrice(new BigDecimal("199.99"));
        response.setImage("pepperoni.png");
        response.setProductType(ProductType.PIZZA);
        response.setProductStatus(ProductStatus.ACTIVE);

        ApiResponse<ProductResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        Messages.PRODUCT_FOUND,
                        "test-trace-id",
                        response,
                        null
                );

        when(productService.getByIdProduct(1L))
                .thenReturn(response);
        when(responseFactory.success(eq(Messages.PRODUCT_FOUND), eq(response)))
                .thenReturn(apiResponse);

        mockMvc
                .perform(
                        get("/api/products/{id}", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.PRODUCT_FOUND))
                .andExpect(jsonPath("$.traceId").value("test-trace-id"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Pizza pepperoni"))
                .andExpect(jsonPath("$.data.description").value("Classic pizza pepperoni"))
                .andExpect(jsonPath("$.data.price").value(199.99))
                .andExpect(jsonPath("$.data.image").value("pepperoni.png"))
                .andExpect(jsonPath("$.data.productType").value("PIZZA"))
                .andExpect(jsonPath("$.data.productStatus").value("ACTIVE"));

        verify(productService).getByIdProduct(1L);
        verify(responseFactory).success(
                eq(Messages.PRODUCT_FOUND), eq(response)
        );
    }

    @Test
    @DisplayName("should return not found when product not found")
    void shouldReturnNotFoundWhenProductNotFound() throws Exception {
        ResourceNotFoundException exception = new ResourceNotFoundException(Messages.PRODUCT_NOT_FOUND);

        ApiErrorResponse errorResponse = new ApiErrorResponse();

        doThrow(exception)
                .when(productService)
                .getByIdProduct(1L);

        when(responseFactory.error(HttpStatus.NOT_FOUND, Messages.PRODUCT_NOT_FOUND))
                .thenReturn(errorResponse);

        mockMvc
                .perform(
                        get("/api/products/{id}", 1L)
                )
                .andExpect(status().isNotFound());

        verify(productService).getByIdProduct(1L);
        verify(responseFactory).error(HttpStatus.NOT_FOUND, Messages.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should get all products successfully")
    void shouldGetAllProductsSuccessfully() throws Exception {
        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setName("Pizza pepperoni");
        response.setDescription("Classic pizza pepperoni");
        response.setPrice(new BigDecimal("199.99"));
        response.setImage("pepperoni.png");
        response.setProductType(ProductType.PIZZA);
        response.setProductStatus(ProductStatus.ACTIVE);

        ApiResponse<List<ProductResponse>> apiResponse =
                new ApiResponse<>(
                        true,
                        Messages.PRODUCTS_RETRIEVED,
                        "test-trace-id",
                        List.of(response),
                        null
                );

        when(productService.getAllProducts())
                .thenReturn(List.of(response));
        when(responseFactory.<List<ProductResponse>>success(
                anyString(),
                anyList()
        )).thenReturn(apiResponse);

        mockMvc
                .perform(
                        get("/api/products")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.PRODUCTS_RETRIEVED))
                .andExpect(jsonPath("$.traceId").value("test-trace-id"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Pizza pepperoni"))
                .andExpect(jsonPath("$.data[0].description").value("Classic pizza pepperoni"))
                .andExpect(jsonPath("$.data[0].price").value(199.99))
                .andExpect(jsonPath("$.data[0].image").value("pepperoni.png"))
                .andExpect(jsonPath("$.data[0].productType").value("PIZZA"))
                .andExpect(jsonPath("$.data[0].productStatus").value("ACTIVE"));

        verify(productService).getAllProducts();
        verify(responseFactory).success(anyString(), anyList());
    }

    @Test
    @DisplayName("Should return empty list when there are no products")
    void shouldReturnEmptyListWhenThereAreNoProducts() throws Exception {
        ApiResponse<List<ProductResponse>> apiResponse =
                new ApiResponse<>(
                        true,
                        Messages.PRODUCTS_RETRIEVED,
                        "test-trace-id",
                        List.of(),
                        null
                );

        when(productService.getAllProducts())
                .thenReturn(List.of());
        when(responseFactory.<List<ProductResponse>>success(
                anyString(),
                anyList()
        )).thenReturn(apiResponse);

        mockMvc
                .perform(
                        get("/api/products")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(productService).getAllProducts();
        verify(responseFactory).success(anyString(), anyList());
    }

    @Test
    @DisplayName("Should update product successfully")
    void shouldUpdateProductSuccessfully() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Pizza pepperoni");
        request.setDescription("Classic pizza pepperoni");
        request.setPrice(new BigDecimal("199.99"));
        request.setImage("pepperoni.png");
        request.setProductType(ProductType.PIZZA);

        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setName("Pizza pepperoni");
        response.setDescription("Classic pizza pepperoni");
        response.setPrice(new BigDecimal("199.99"));
        response.setImage("pepperoni.png");
        response.setProductType(ProductType.PIZZA);
        response.setProductStatus(ProductStatus.ACTIVE);

        ApiResponse<ProductResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        Messages.PRODUCT_UPDATED,
                        "test-trace-id",
                        response,
                        null
                );

        when(productService.updateProduct(eq(1L), any(ProductRequest.class)))
                .thenReturn(response);
        when(responseFactory.success(eq(Messages.PRODUCT_UPDATED), eq(response)))
                .thenReturn(apiResponse);

        mockMvc
                .perform(
                        put("/api/products/{id}", 1L)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.PRODUCT_UPDATED))
                .andExpect(jsonPath("$.traceId").value("test-trace-id"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Pizza pepperoni"))
                .andExpect(jsonPath("$.data.description").value("Classic pizza pepperoni"))
                .andExpect(jsonPath("$.data.price").value(199.99))
                .andExpect(jsonPath("$.data.image").value("pepperoni.png"))
                .andExpect(jsonPath("$.data.productType").value("PIZZA"))
                .andExpect(jsonPath("$.data.productStatus").value("ACTIVE"));

        verify(productService).updateProduct(eq(1L), any(ProductRequest.class));
        verify(responseFactory).success(eq(Messages.PRODUCT_UPDATED), eq(response));
    }

    @Test
    @DisplayName("Should return bad request when update request is invalid")
    void shouldReturnBadRequestWhenUpdateRequestIsInvalid() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("");
        request.setDescription("");
        request.setPrice(new BigDecimal(0));
        request.setImage("");
        request.setProductType(null);

        mockMvc
                .perform(
                        put("/api/products/{id}", 1L)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("Should return not found when update product does not exist")
    void shouldReturnNotFoundWhenUpdateProductDoesNotExist() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Pizza pepperoni");
        request.setDescription("Classic pizza pepperoni");
        request.setPrice(new BigDecimal("199.99"));
        request.setImage("pepperoni.png");
        request.setProductType(ProductType.PIZZA);

        ApiErrorResponse errorResponse = new ApiErrorResponse();

        when(productService.updateProduct(eq(99L), any(ProductRequest.class)))
                .thenThrow(new ResourceNotFoundException(Messages.PRODUCT_NOT_FOUND));
        when(responseFactory.error(HttpStatus.NOT_FOUND, Messages.PRODUCT_NOT_FOUND))
                .thenReturn(errorResponse);

        mockMvc
                .perform(
                        put("/api/products/{id}", 99L)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());

        verify(productService).updateProduct(eq(99L), any(ProductRequest.class));
        verify(responseFactory).error(HttpStatus.NOT_FOUND, Messages.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should return conflict when update product already exists")
    void shouldReturnConflictWhenUpdateProductAlreadyExists() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Pizza pepperoni");
        request.setDescription("Classic pizza pepperoni");
        request.setPrice(new BigDecimal("199.99"));
        request.setImage("pepperoni.png");
        request.setProductType(ProductType.PIZZA);

        ApiErrorResponse errorResponse = new ApiErrorResponse();

        when(productService.updateProduct(eq(1L), any(ProductRequest.class)))
                .thenThrow(new ConflictException(Messages.PRODUCT_ALREADY_EXISTS));
        when(responseFactory.error(HttpStatus.CONFLICT, Messages.PRODUCT_ALREADY_EXISTS))
                .thenReturn(errorResponse);

        mockMvc
                .perform(
                        put("/api/products/{id}", 1L)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict());

        verify(productService).updateProduct(eq(1L), any(ProductRequest.class));
        verify(responseFactory).error(HttpStatus.CONFLICT, Messages.PRODUCT_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("Should soft delete product successfully")
    void shouldSoftDeleteProductSuccessfully() throws Exception {
        ApiResponse<Void> apiResponse =
                new ApiResponse<>(
                        true,
                        Messages.PRODUCT_DELETED,
                        "test-trace-id",
                        null,
                        null
                );

        doNothing()
                .when(productService)
                .deleteProduct(1L);
        when(responseFactory.<Void>success(
                anyString(),
                isNull()
        )).thenReturn(apiResponse);

        mockMvc
                .perform(
                        delete("/api/products/{id}", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.PRODUCT_DELETED))
                .andExpect(jsonPath("$.traceId").value("test-trace-id"));

        verify(productService).deleteProduct(1L);
        verify(responseFactory).success(anyString(), isNull());
    }

    @Test
    @DisplayName("Should delete return not found when product does not exist")
    void shouldDeleteReturnNotFoundWhenProductDoesNotExist() throws Exception {
        ResourceNotFoundException exception =
                new ResourceNotFoundException(Messages.PRODUCT_NOT_FOUND);

        ApiErrorResponse errorResponse = new ApiErrorResponse();

        doThrow(exception)
                .when(productService)
                .deleteProduct(999L);

        when(responseFactory.error(HttpStatus.NOT_FOUND, Messages.PRODUCT_NOT_FOUND))
                .thenReturn(errorResponse);

        mockMvc
                .perform(
                        delete("/api/products/{id}", 999L)
                )
                .andExpect(status().isNotFound());

        verify(productService).deleteProduct(999L);
        verify(responseFactory).error(HttpStatus.NOT_FOUND, Messages.PRODUCT_NOT_FOUND);
    }
}
