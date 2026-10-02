package com.msvc.catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msvc.catalog.dto.pizza.request.PizzaRequest;
import com.msvc.catalog.dto.pizza.response.PizzaResponse;
import com.msvc.catalog.service.pizza.PizzaService;
import com.msvc.catalog.shared.constans.Messages;
import com.msvc.catalog.shared.exception.ConflictException;
import com.msvc.catalog.shared.exception.ResourceNotFoundException;
import com.msvc.catalog.shared.responses.ApiErrorResponse;
import com.msvc.catalog.shared.responses.ApiResponse;
import com.msvc.catalog.shared.responses.ResponseFactory;
import com.msvc.catalog.support.PizzaTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PizzaController.class)
public class PizzaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PizzaService pizzaService;

    @MockitoBean
    private ResponseFactory responseFactory;

    // ============================================================
    // POST /api/pizzas — CREATE
    // ============================================================

    @Test
    @DisplayName("Should create pizza successfully")
    void shouldCreatePizzaSuccessfully() throws Exception {
        // Given
        PizzaRequest request = PizzaTestDataBuilder.aPizza().buildRequest();
        PizzaResponse response = PizzaTestDataBuilder.aPizza().withId(1L).buildResponse();

        when(pizzaService.createPizza(any(PizzaRequest.class)))
                .thenReturn(response);

        ApiResponse<PizzaResponse> apiResponse =
                new ApiResponse<>(
                true,
                        Messages.PIZZA_CREATED,
                        "test-trace-id",
                        response,
                        null
                );

        when(responseFactory.success(anyString(), any(PizzaResponse.class)))
                .thenReturn(apiResponse);

        // When & Then
        mockMvc
                .perform(
                        post("/api/pizzas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.PIZZA_CREATED))
                .andExpect(jsonPath("$.traceId").value("test-trace-id"))
                .andExpect(jsonPath("$.data.id").value(1L));

        verify(pizzaService).createPizza(any(PizzaRequest.class));
        verify(responseFactory).success(anyString(), any(PizzaResponse.class));
    }

    @Test
    @DisplayName("Should return 400 when create request is invalid")
    void shouldReturnBadRequestWhenCreateRequestIsInvalid() throws Exception {
        // Given — request inválido (faltan campos)
        PizzaRequest request = new PizzaRequest();

        // When & Then
        mockMvc
                .perform(
                        post("/api/pizzas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(pizzaService, never()).createPizza(any());
    }

    @Test
    @DisplayName("Should return 404 when product not found during create")
    void shouldReturnNotFoundWhenProductNotFoundDuringCreate() throws Exception {
        // Given
        PizzaRequest request = PizzaTestDataBuilder.aPizza().buildRequest();

        when(pizzaService.createPizza(any(PizzaRequest.class)))
                .thenThrow(new ResourceNotFoundException(Messages.PRODUCT_NOT_FOUND));

        ApiErrorResponse errorResponse = new ApiErrorResponse();

        when(responseFactory.error(HttpStatus.NOT_FOUND, Messages.PRODUCT_NOT_FOUND))
                .thenReturn(errorResponse);

        // When & Then
        mockMvc
                .perform(
                        post("/api/pizzas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());

        verify(pizzaService).createPizza(any(PizzaRequest.class));
        verify(responseFactory).error(HttpStatus.NOT_FOUND, Messages.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 409 when pizza already exists")
    void shouldReturnConflictWhenPizzaAlreadyExists() throws Exception {
        // Given
        PizzaRequest request = PizzaTestDataBuilder.aPizza().buildRequest();

        // ⬇️ CAMBIO: usar ConflictException, no BusinessException
        when(pizzaService.createPizza(any(PizzaRequest.class)))
                .thenThrow(new ConflictException(Messages.PIZZA_ALREADY_EXISTS));

        ApiErrorResponse errorResponse = new ApiErrorResponse();

        when(responseFactory.error(HttpStatus.CONFLICT, Messages.PIZZA_ALREADY_EXISTS))
                .thenReturn(errorResponse);

        // When & Then
        mockMvc
                .perform(
                        post("/api/pizzas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict());

        verify(pizzaService).createPizza(any(PizzaRequest.class));
    }

    // ============================================================
    // GET /api/pizzas/{id} — FIND BY ID
    // ============================================================

    @Test
    @DisplayName("Should return pizza by id successfully")
    void shouldReturnPizzaByIdSuccessfully() throws Exception {
        // Given
        Long pizzaId = 1L;
        PizzaResponse response = PizzaTestDataBuilder
                .aPizza()
                .withId(pizzaId)
                .buildResponse();

        when(pizzaService.getPizzaById(pizzaId))
                .thenReturn(response);

        ApiResponse<PizzaResponse> apiResponse =
                new ApiResponse<>(
                true,
                        Messages.PIZZA_FOUND,
                        "test-trace-id",
                        response,
                        null
                );

        when(responseFactory.success(anyString(), any(PizzaResponse.class)))
                .thenReturn(apiResponse);

        // When & Then
        mockMvc
                .perform(
                        get("/api/pizzas/{id}", pizzaId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.PIZZA_FOUND))
                .andExpect(jsonPath("$.data.id").value(pizzaId));

        verify(pizzaService).getPizzaById(pizzaId);
        verify(responseFactory).success(anyString(), any(PizzaResponse.class));
    }

    @Test
    @DisplayName("Should return 404 when pizza not found by id")
    void shouldReturnNotFoundWhenPizzaNotFoundById() throws Exception {
        // Given
        Long pizzaId = 999L;

        when(pizzaService.getPizzaById(pizzaId))
                .thenThrow(new ResourceNotFoundException(Messages.PIZZA_NOT_FOUND));

        ApiErrorResponse errorResponse = new ApiErrorResponse();
        when(responseFactory.error(HttpStatus.NOT_FOUND, Messages.PIZZA_NOT_FOUND))
                .thenReturn(errorResponse);

        // When & Then
        mockMvc
                .perform(
                        get("/api/pizzas/{id}", pizzaId)
                )
                .andExpect(status().isNotFound());

        verify(pizzaService).getPizzaById(pizzaId);
        verify(responseFactory).error(HttpStatus.NOT_FOUND, Messages.PIZZA_NOT_FOUND);
    }

    // ============================================================
    // GET /api/pizzas — GET ALL
    // ============================================================

    @Test
    @DisplayName("Should return all pizzas successfully")
    void shouldReturnAllPizzasSuccessfully() throws Exception {
        // Given
        List<PizzaResponse> responses = List.of(
                PizzaTestDataBuilder.aPizza().withId(1L).buildResponse(),
                PizzaTestDataBuilder.aPizza().withId(2L).buildResponse()
        );

        when(pizzaService.getAllPizzas())
                .thenReturn(responses);

        ApiResponse<List<PizzaResponse>> apiResponse =
                new ApiResponse<>(
                true,
                        Messages.PIZZA_RETRIEVED,
                        "test-trace-id",
                        responses,
                        null
                );

        when(responseFactory.<List<PizzaResponse>>success(anyString(), anyList()))
                .thenReturn(apiResponse);

        // When & Then
        mockMvc
                .perform(
                        get("/api/pizzas")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.PIZZA_RETRIEVED))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[1].id").value(2L));

        verify(pizzaService).getAllPizzas();
        verify(responseFactory).success(anyString(), anyList());
    }

    @Test
    @DisplayName("Should return empty list when no pizzas exist")
    void shouldReturnEmptyListWhenNoPizzasExist() throws Exception {
        // Given
        when(pizzaService.getAllPizzas()).thenReturn(List.of());

        ApiResponse<List<PizzaResponse>> apiResponse =
                new ApiResponse<>(
                true,
                        Messages.PIZZA_RETRIEVED,
                        "test-trace-id",
                        List.of(),
                        null
                );

        when(responseFactory.<List<PizzaResponse>>success(anyString(), anyList()))
                .thenReturn(apiResponse);

        // When & Then
        mockMvc
                .perform(
                        get("/api/pizzas")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(pizzaService).getAllPizzas();
    }

    // ============================================================
    // PUT /api/pizzas/{id} — UPDATE
    // ============================================================

    @Test
    @DisplayName("Should update pizza successfully")
    void shouldUpdatePizzaSuccessfully() throws Exception {
        // Given
        Long pizzaId = 1L;
        PizzaRequest request = PizzaTestDataBuilder.aPizza().buildRequest();
        PizzaResponse response = PizzaTestDataBuilder
                .aPizza()
                .withId(pizzaId)
                .buildResponse();

        when(pizzaService.updatePizza(eq(pizzaId), any(PizzaRequest.class)))
                .thenReturn(response);

        ApiResponse<PizzaResponse> apiResponse =
                new ApiResponse<>(
                true,
                        Messages.PIZZA_UPDATED,
                        "test-trace-id",
                        response,
                        null
                );

        when(responseFactory.success(anyString(), any(PizzaResponse.class)))
                .thenReturn(apiResponse);

        // When & Then
        mockMvc
                .perform(
                        put("/api/pizzas/{id}", pizzaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.PIZZA_UPDATED))
                .andExpect(jsonPath("$.data.id").value(pizzaId));

        verify(pizzaService).updatePizza(eq(pizzaId), any(PizzaRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when update request is invalid")
    void shouldReturnBadRequestWhenUpdateRequestIsInvalid() throws Exception {
        // Given
        PizzaRequest request = new PizzaRequest(); // inválido

        // When & Then
        mockMvc
                .perform(
                        put("/api/pizzas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(pizzaService, never()).updatePizza(anyLong(), any());
    }

    @Test
    @DisplayName("Should return 404 when pizza not found during update")
    void shouldReturnNotFoundWhenPizzaNotFoundDuringUpdate() throws Exception {
        // Given
        Long pizzaId = 999L;
        PizzaRequest request = PizzaTestDataBuilder.aPizza().buildRequest();

        when(pizzaService.updatePizza(eq(pizzaId), any(PizzaRequest.class)))
                .thenThrow(new ResourceNotFoundException(Messages.PIZZA_NOT_FOUND));

        ApiErrorResponse errorResponse = new ApiErrorResponse();
        when(responseFactory.error(HttpStatus.NOT_FOUND, Messages.PIZZA_NOT_FOUND))
                .thenReturn(errorResponse);

        // When & Then
        mockMvc
                .perform(
                        put("/api/pizzas/{id}", pizzaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());

        verify(pizzaService).updatePizza(eq(pizzaId), any(PizzaRequest.class));
    }

    @Test
    @DisplayName("Should return 409 when pizza already exists during update")
    void shouldReturnConflictWhenPizzaAlreadyExistsDuringUpdate() throws Exception {
        // Given
        Long pizzaId = 1L;
        PizzaRequest request = PizzaTestDataBuilder.aPizza().buildRequest();

        when(pizzaService.updatePizza(eq(pizzaId), any(PizzaRequest.class)))
                .thenThrow(new ConflictException(Messages.PIZZA_ALREADY_EXISTS));

        ApiErrorResponse errorResponse = new ApiErrorResponse();
        when(responseFactory.error(HttpStatus.CONFLICT, Messages.PIZZA_ALREADY_EXISTS))
                .thenReturn(errorResponse);

        // When & Then
        mockMvc
                .perform(
                        put("/api/pizzas/{id}", pizzaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict());

        verify(pizzaService).updatePizza(eq(pizzaId), any(PizzaRequest.class));
    }

    // ============================================================
    // DELETE /api/pizzas/{id} — DELETE
    // ============================================================

    @Test
    @DisplayName("Should delete pizza successfully")
    void shouldDeletePizzaSuccessfully() throws Exception {
        // Given
        Long pizzaId = 1L;

        doNothing().when(pizzaService).deletePizzaById(pizzaId);

        ApiResponse<Void> apiResponse =
                new ApiResponse<>(
                true,
                        Messages.PIZZA_DELETED,
                        "test-trace-id",
                        null,
                        null
                );

        when(responseFactory.<Void>success(anyString(), isNull()))
                .thenReturn(apiResponse);

        // When & Then
        mockMvc
                .perform(
                        delete("/api/pizzas/{id}", pizzaId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.PIZZA_DELETED));

        verify(pizzaService).deletePizzaById(pizzaId);
        verify(responseFactory).success(anyString(), isNull());
    }

    @Test
    @DisplayName("Should return 404 when pizza not found during delete")
    void shouldReturnNotFoundWhenPizzaNotFoundDuringDelete() throws Exception {
        // Given
        Long pizzaId = 999L;

        doThrow(new ResourceNotFoundException(Messages.PIZZA_NOT_FOUND))
                .when(pizzaService).deletePizzaById(pizzaId);

        ApiErrorResponse errorResponse = new ApiErrorResponse();
        when(responseFactory.error(HttpStatus.NOT_FOUND, Messages.PIZZA_NOT_FOUND))
                .thenReturn(errorResponse);

        // When & Then
        mockMvc
                .perform(
                        delete("/api/pizzas/{id}", pizzaId)
                )
                .andExpect(status().isNotFound());

        verify(pizzaService).deletePizzaById(pizzaId);
        verify(responseFactory).error(HttpStatus.NOT_FOUND, Messages.PIZZA_NOT_FOUND);
    }
}