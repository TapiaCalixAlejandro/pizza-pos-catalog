package com.msvc.catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msvc.catalog.dto.dessert.request.DessertRequest;
import com.msvc.catalog.dto.dessert.response.DessertResponse;
import com.msvc.catalog.enums.PortionUnit;
import com.msvc.catalog.service.dessert.DessertService;
import com.msvc.catalog.shared.constans.Messages;
import com.msvc.catalog.shared.exception.BusinessException;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DessertController.class)
public class DessertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private DessertService dessertService;

    @MockitoBean
    private ResponseFactory responseFactory;

    @Test
    @DisplayName("Should create dessert successfully")
    void shouldCreateDessertSuccessfully() throws Exception {

        // Arrange
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

        ApiResponse<DessertResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        Messages.DESSERT_CREATED,
                        "test-trace-id",
                        response,
                        null
                );

        when(dessertService.createDessert(any(DessertRequest.class)))
                .thenReturn(response);

        when(responseFactory.success(
                eq(Messages.DESSERT_CREATED),
                eq(response)
        )).thenReturn(apiResponse);

        // Act & Assert
        mockMvc
                .perform(
                        post("/api/desserts")
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.DESSERT_CREATED))
                .andExpect(jsonPath("$.traceId").value("test-trace-id"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.productId").value(1))
                .andExpect(jsonPath("$.data.productName").value("Pastel de chocolate"))
                .andExpect(jsonPath("$.data.portion").value(1))
                .andExpect(jsonPath("$.data.portionUnit").value("PIECE"));

        verify(dessertService).createDessert(any(DessertRequest.class));
        verify(responseFactory).success(
                eq(Messages.DESSERT_CREATED),
                eq(response)
        );
    }

    @Test
    @DisplayName("Should return 400 when dessert request is invalid")
    void shouldReturnBadRequestCreateDessertFailed() throws Exception {
        DessertRequest request = new DessertRequest();
        request.setProductId(0L);
        request.setPortion(0);
        request.setPortionUnit(null);

        mockMvc
                .perform(
                        post("/api/desserts")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(dessertService);
    }

    @Test
    @DisplayName("Should return 404 when product does not exist")
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        DessertRequest request = new DessertRequest();
        request.setProductId(9L);
        request.setPortion(1);
        request.setPortionUnit(PortionUnit.PIECE);

        when(dessertService.createDessert(any(DessertRequest.class)))
                .thenThrow(new ResourceNotFoundException(Messages.PRODUCT_NOT_FOUND));

        mockMvc
                .perform(
                        post("/api/desserts")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());

        verify(dessertService).createDessert(any(DessertRequest.class));
    }

    @Test
    @DisplayName("Should return conflict when dessert already exists")
    void shouldReturnConflictWhenDessertAlreadyExists() throws Exception {
        DessertRequest request = new DessertRequest();
        request.setProductId(1L);
        request.setPortion(1);
        request.setPortionUnit(PortionUnit.PIECE);

        when(dessertService.createDessert(any(DessertRequest.class)))
                .thenThrow(new ConflictException(Messages.DESSERT_ALREADY_EXISTS));

        mockMvc
                .perform(
                        post("/api/desserts")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict());

        verify(dessertService).createDessert(any(DessertRequest.class));
    }

    @Test
    @DisplayName("Should get dessert successfully")
    void shouldGetDessertSuccessfully() throws Exception {
        DessertResponse response = new DessertResponse();
        response.setId(1L);
        response.setPortion(1);
        response.setProductId(1L);
        response.setProductName("Pastel de chocolate");
        response.setPortionUnit(PortionUnit.PIECE);

        ApiResponse<DessertResponse> apiResponse = new ApiResponse<>(
                true,
                Messages.DESSERT_FOUND,
                "test-trace-id",
                response,
                null
        );

        when(dessertService.findDessertById(1L))
                .thenReturn(response);
        when(responseFactory.success(
                anyString(),
                any(DessertResponse.class)
        )).thenReturn(apiResponse);

        mockMvc
                .perform(
                        get("/api/desserts/{id}", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.DESSERT_FOUND))
                .andExpect(jsonPath("$.traceId").value("test-trace-id"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.portion").value(1))
                .andExpect(jsonPath("$.data.productId").value(1))
                .andExpect(jsonPath("$.data.productName").value("Pastel de chocolate"))
                .andExpect(jsonPath("$.data.portionUnit").value("PIECE"));

        verify(dessertService).findDessertById(1L);
        verify(responseFactory).success(anyString(), eq(response));
    }

    @Test
    @DisplayName("Should return not found when dessert does not exist")
    void shouldReturnNotFoundWhenDessertDoesNotExist() throws Exception {
        ResourceNotFoundException exception =
                new ResourceNotFoundException(Messages.DESSERT_NOT_FOUND);
        ApiErrorResponse errorResponse = new ApiErrorResponse();

        doThrow(exception)
                .when(dessertService)
                .findDessertById(999L);

        when(responseFactory
                .error(
                        HttpStatus.NOT_FOUND,
                        Messages.DESSERT_NOT_FOUND
                )
        )
                .thenReturn(errorResponse);

        mockMvc
                .perform(
                        get("/api/desserts/{id}", 999L)
                )
                .andExpect(status().isNotFound());

        verify(dessertService).findDessertById(999L);
        verify(responseFactory).error(HttpStatus.NOT_FOUND, Messages.DESSERT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should get all desserts successfully")
    void shouldGetAllDessertsSuccessfully() throws Exception {
        DessertResponse response = new DessertResponse();
        response.setId(1L);
        response.setPortion(1);
        response.setProductId(1L);
        response.setProductName("Pastel de chocolate");
        response.setPortionUnit(PortionUnit.PIECE);

        ApiResponse<List<DessertResponse>> apiResponse = new ApiResponse<>(
                true,
                Messages.DESSERT_RETRIEVED,
                "test-trace-id",
                List.of(response),
                null
        );

        when(dessertService.findAllDesserts())
                .thenReturn(List.of(response));
        when(responseFactory.<List<DessertResponse>>success(
                anyString(),
                anyList()
        )).thenReturn(apiResponse);

        mockMvc
                .perform(
                        get("/api/desserts")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.traceId").value("test-trace-id"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].portion").value(1))
                .andExpect(jsonPath("$.data[0].productId").value(1))
                .andExpect(jsonPath("$.data[0].productName").value("Pastel de chocolate"))
                .andExpect(jsonPath("$.data[0].portionUnit").value("PIECE"));

        verify(dessertService).findAllDesserts();
        verify(responseFactory).success(anyString(), eq(List.of(response)));
    }

    @Test
    @DisplayName("Should return empty list when there are no desserts")
    void shouldReturnEmptyListWhenThereAreNoDesserts() throws Exception {
        ApiResponse<List<DessertResponse>> apiResponse = new ApiResponse<>(
                true,
                Messages.DESSERT_RETRIEVED,
                "test-trace-id",
                List.of(),
                null
        );

        when(dessertService.findAllDesserts())
                .thenReturn(List.of());
        when(responseFactory.<List<DessertResponse>>success(
                anyString(),
                anyList()
        )).thenReturn(apiResponse);

        mockMvc
                .perform(
                        get("/api/desserts")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(dessertService).findAllDesserts();
        verify(responseFactory).success(anyString(), eq(List.of()));
    }

    @Test
    @DisplayName("Should update dessert successfully")
    void shouldUpdateDessertSuccessfully() throws Exception {
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

        ApiResponse<DessertResponse> apiResponse = new ApiResponse<>(
                true,
                Messages.DESSERT_UPDATED,
                "test-trace-id",
                response,
                null
        );

        when(dessertService.updateDessert(eq(1L), any(DessertRequest.class)))
                .thenReturn(response);
        when(responseFactory.success(
                eq(Messages.DESSERT_UPDATED),
                eq(response)
        )).thenReturn(apiResponse);

        mockMvc
                .perform(
                        put("/api/desserts/{id}", 1L)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.DESSERT_UPDATED))
                .andExpect(jsonPath("$.traceId").value("test-trace-id"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.productId").value(1))
                .andExpect(jsonPath("$.data.productName").value("Pastel de chocolate"))
                .andExpect(jsonPath("$.data.portion").value(1))
                .andExpect(jsonPath("$.data.portionUnit").value("PIECE"));

        verify(dessertService).updateDessert(eq(1L), any(DessertRequest.class));
        verify(responseFactory).success(
                eq(Messages.DESSERT_UPDATED),
                eq(response)
        );
    }

    @Test
    @DisplayName("Should return bad request when update request is invalid")
    void shouldReturnBadRequestWhenUpdateRequestIsInvalid() throws Exception {
        DessertRequest request = new DessertRequest();
        request.setPortion(0);
        request.setProductId(0L);
        request.setPortionUnit(null);

        mockMvc
                .perform(
                        put("/api/desserts/{id}", 1L)
                                .contentType("application/json")
                                .content(objectMapper
                                        .writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(dessertService);
    }

    @Test
    @DisplayName("Should return 404 when update product does not exist")
    void shouldReturnNotFoundWhenUpdateProductDoesNotExist() throws Exception {
            DessertRequest request = new DessertRequest();
            request.setPortion(1);
            request.setProductId(1L);
            request.setPortionUnit(PortionUnit.PIECE);

            ApiErrorResponse errorResponse = new ApiErrorResponse();

            when(dessertService.updateDessert(eq(1L), any(DessertRequest.class)))
                    .thenThrow(new ResourceNotFoundException(Messages.PRODUCT_NOT_FOUND));

            when(responseFactory
                    .error(
                            HttpStatus.NOT_FOUND,
                            Messages.PRODUCT_NOT_FOUND
                    )
            ).thenReturn(errorResponse);

            mockMvc
                    .perform(
                            put("/api/desserts/{id}", 1L)
                                    .contentType("application/json")
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isNotFound());

            verify(dessertService).updateDessert(eq(1L), any(DessertRequest.class));
            verify(responseFactory).error(HttpStatus.NOT_FOUND, Messages.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should update return not found when dessert does not exist")
    void shouldUpdateReturnNotFoundWhenDessertDoesNotExist() throws Exception {
        DessertRequest request = new DessertRequest();
        request.setPortion(1);
        request.setProductId(1L);
        request.setPortionUnit(PortionUnit.PIECE);

        when(dessertService.updateDessert(eq(999L), any(DessertRequest.class)))
                .thenThrow(new ResourceNotFoundException(Messages.DESSERT_NOT_FOUND));

        mockMvc
                .perform(
                        put("/api/desserts/{id}", 999L)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());

        verify(dessertService).updateDessert(eq(999L), any(DessertRequest.class));
    }

    @Test
    @DisplayName("Should return bad request when update product is not dessert")
    void shouldReturnBadRequestWhenUpdateProductIsNotDessert() throws Exception {
        DessertRequest request = new DessertRequest();
        request.setPortion(1);
        request.setProductId(1L);
        request.setPortionUnit(PortionUnit.PIECE);

        when(dessertService.updateDessert(eq(1L), any(DessertRequest.class)))
                .thenThrow(new BusinessException(Messages.PRODUCT_MUST_BE_DESSERT));

        mockMvc
                .perform(
                        put("/api/desserts/{id}", 1L)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(dessertService).updateDessert(eq(1L), any(DessertRequest.class));
    }

    @Test
    @DisplayName("Should return 409 when updating dessert causes conflict")
    void shouldReturnConflictWhenProductAlreadyHasDessert() throws Exception {
        DessertRequest request = new DessertRequest();
        request.setPortion(1);
        request.setProductId(1L);
        request.setPortionUnit(PortionUnit.PIECE);

        when(dessertService.updateDessert(eq(1L), any(DessertRequest.class)))
                .thenThrow(new ConflictException(Messages.DESSERT_ALREADY_EXISTS));

        mockMvc
                .perform(
                        put("/api/desserts/{id}", 1L)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict());

        verify(dessertService).updateDessert(eq(1L), any(DessertRequest.class));
    }

    @Test
    @DisplayName("Should delete dessert by id successfully")
    void shouldDeleteDessertSuccessfully() throws Exception {
        ApiResponse<Void> apiResponse = new ApiResponse<>(
                true,
                Messages.DESSERT_DELETED,
                "test-trace-id",
                null,
                null
        );

        doNothing()
                .when(dessertService)
                .deleteDessertById(1L);

        when(responseFactory.<Void>success(
                anyString(),
                isNull()
        )).thenReturn(apiResponse);

        mockMvc
                .perform(
                        delete("/api/desserts/{id}", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.DESSERT_DELETED))
                .andExpect(jsonPath("$.traceId").value("test-trace-id"));

        verify(dessertService).deleteDessertById(1L);
        verify(responseFactory).success(anyString(), isNull());
    }

    @Test
    @DisplayName("Should delete return 404 when dessert does not exist")
    void shouldDeleteReturnNotFoundWhenDessertDoesNotExist() throws Exception {
        ResourceNotFoundException exception =
                new ResourceNotFoundException(Messages.DESSERT_NOT_FOUND);

        ApiErrorResponse errorResponse = new ApiErrorResponse();

        doThrow(exception)
                .when(dessertService)
                .deleteDessertById(999L);

        when(responseFactory.error(
                HttpStatus.NOT_FOUND,
                Messages.DESSERT_NOT_FOUND
        )).thenReturn(errorResponse);

        mockMvc
                .perform(
                        delete("/api/desserts/{id}", 999L)
                )
                .andExpect(status().isNotFound());

        verify(dessertService).deleteDessertById(999L);
        verify(responseFactory).error(HttpStatus.NOT_FOUND, Messages.DESSERT_NOT_FOUND);
    }

}