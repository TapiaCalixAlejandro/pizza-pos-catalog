package com.msvc.catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msvc.catalog.dto.drink.request.DrinkRequest;
import com.msvc.catalog.dto.drink.response.DrinkResponse;
import com.msvc.catalog.service.drink.DrinkService;
import com.msvc.catalog.shared.constans.Messages;
import com.msvc.catalog.shared.exception.ConflictException;
import com.msvc.catalog.shared.exception.ResourceNotFoundException;
import com.msvc.catalog.shared.responses.ApiErrorResponse;
import com.msvc.catalog.shared.responses.ApiResponse;
import com.msvc.catalog.shared.responses.ResponseFactory;
import com.msvc.catalog.support.DrinkTestDataBuilder;
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

@WebMvcTest(DrinkController.class)
public class DrinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private DrinkService drinkService;

    @MockitoBean
    private ResponseFactory responseFactory;

    // ============================================================
    // POST /api/drinks — CREATE
    // ============================================================

    @Test
    @DisplayName("Should create drink successfully")
    void shouldCreateDrinkSuccessfully() throws Exception {
        // Given
        DrinkRequest request = DrinkTestDataBuilder.aDrink().buildRequest();
        DrinkResponse response = DrinkTestDataBuilder.aDrink().withId(1L).buildResponse();

        when(drinkService.createDrink(any(DrinkRequest.class)))
                .thenReturn(response);

        ApiResponse<DrinkResponse> apiResponse = new ApiResponse<>(
                true, Messages.DRINK_CREATED, "test-trace-id", response, null
        );

        when(responseFactory.success(anyString(), any(DrinkResponse.class)))
                .thenReturn(apiResponse);

        // When & Then
        mockMvc.perform(post("/api/drinks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.DRINK_CREATED))
                .andExpect(jsonPath("$.data.id").value(1L));

        verify(drinkService).createDrink(any(DrinkRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when create request is invalid")
    void shouldReturnBadRequestWhenCreateRequestIsInvalid() throws Exception {
        // Given — request inválido
        DrinkRequest request = new DrinkRequest();

        // When & Then
        mockMvc.perform(post("/api/drinks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(drinkService, never()).createDrink(any());
    }

    @Test
    @DisplayName("Should return 404 when product not found during create")
    void shouldReturnNotFoundWhenProductNotFoundDuringCreate() throws Exception {
        // Given
        DrinkRequest request = DrinkTestDataBuilder.aDrink().buildRequest();

        when(drinkService.createDrink(any(DrinkRequest.class)))
                .thenThrow(new ResourceNotFoundException(Messages.PRODUCT_NOT_FOUND));

        ApiErrorResponse errorResponse = new ApiErrorResponse();
        when(responseFactory.error(HttpStatus.NOT_FOUND, Messages.PRODUCT_NOT_FOUND))
                .thenReturn(errorResponse);

        // When & Then
        mockMvc.perform(post("/api/drinks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(drinkService).createDrink(any(DrinkRequest.class));
    }

    @Test
    @DisplayName("Should return 409 when drink already exists")
    void shouldReturnConflictWhenDrinkAlreadyExists() throws Exception {
        // Given
        DrinkRequest request = DrinkTestDataBuilder.aDrink().buildRequest();

        when(drinkService.createDrink(any(DrinkRequest.class)))
                .thenThrow(new ConflictException(Messages.DRINK_ALREADY_EXISTS));

        ApiErrorResponse errorResponse = new ApiErrorResponse();
        when(responseFactory.error(HttpStatus.CONFLICT, Messages.DRINK_ALREADY_EXISTS))
                .thenReturn(errorResponse);

        // When & Then
        mockMvc.perform(post("/api/drinks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(drinkService).createDrink(any(DrinkRequest.class));
    }

    // ============================================================
    // GET /api/drinks/{id}
    // ============================================================

    @Test
    @DisplayName("Should return drink by id successfully")
    void shouldReturnDrinkByIdSuccessfully() throws Exception {
        // Given
        Long drinkId = 1L;
        DrinkResponse response = DrinkTestDataBuilder.aDrink().withId(drinkId).buildResponse();

        when(drinkService.getDrinkById(drinkId)).thenReturn(response);

        ApiResponse<DrinkResponse> apiResponse = new ApiResponse<>(
                true, Messages.DRINK_FOUND, "test-trace-id", response, null
        );

        when(responseFactory.success(anyString(), any(DrinkResponse.class)))
                .thenReturn(apiResponse);

        // When & Then
        mockMvc.perform(get("/api/drinks/{id}", drinkId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.DRINK_FOUND))
                .andExpect(jsonPath("$.data.id").value(drinkId));

        verify(drinkService).getDrinkById(drinkId);
    }

    @Test
    @DisplayName("Should return 404 when drink not found by id")
    void shouldReturnNotFoundWhenDrinkNotFoundById() throws Exception {
        // Given
        Long drinkId = 999L;

        when(drinkService.getDrinkById(drinkId))
                .thenThrow(new ResourceNotFoundException(Messages.DRINK_NOT_FOUND));

        ApiErrorResponse errorResponse = new ApiErrorResponse();
        when(responseFactory.error(HttpStatus.NOT_FOUND, Messages.DRINK_NOT_FOUND))
                .thenReturn(errorResponse);

        // When & Then
        mockMvc.perform(get("/api/drinks/{id}", drinkId))
                .andExpect(status().isNotFound());

        verify(drinkService).getDrinkById(drinkId);
    }

    // ============================================================
    // GET /api/drinks
    // ============================================================

    @Test
    @DisplayName("Should return all drinks successfully")
    void shouldReturnAllDrinksSuccessfully() throws Exception {
        // Given
        List<DrinkResponse> responses = List.of(
                DrinkTestDataBuilder.aDrink().withId(1L).buildResponse(),
                DrinkTestDataBuilder.aDrink().withId(2L).buildResponse()
        );

        when(drinkService.getAllDrinks()).thenReturn(responses);

        ApiResponse<List<DrinkResponse>> apiResponse = new ApiResponse<>(
                true, Messages.DRINK_RETRIEVED, "test-trace-id", responses, null
        );

        when(responseFactory.<List<DrinkResponse>>success(anyString(), anyList()))
                .thenReturn(apiResponse);

        // When & Then
        mockMvc.perform(get("/api/drinks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[1].id").value(2L));

        verify(drinkService).getAllDrinks();
    }

    @Test
    @DisplayName("Should return empty list when no drinks exist")
    void shouldReturnEmptyListWhenNoDrinksExist() throws Exception {
        // Given
        when(drinkService.getAllDrinks()).thenReturn(List.of());

        ApiResponse<List<DrinkResponse>> apiResponse = new ApiResponse<>(
                true, Messages.DRINK_RETRIEVED, "test-trace-id", List.of(), null
        );

        when(responseFactory.<List<DrinkResponse>>success(anyString(), anyList()))
                .thenReturn(apiResponse);

        // When & Then
        mockMvc.perform(get("/api/drinks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(drinkService).getAllDrinks();
    }

    // ============================================================
    // PUT /api/drinks/{id}
    // ============================================================

    @Test
    @DisplayName("Should update drink successfully")
    void shouldUpdateDrinkSuccessfully() throws Exception {
        // Given
        Long drinkId = 1L;
        DrinkRequest request = DrinkTestDataBuilder.aDrink().buildRequest();
        DrinkResponse response = DrinkTestDataBuilder.aDrink().withId(drinkId).buildResponse();

        when(drinkService.updateDrink(eq(drinkId), any(DrinkRequest.class)))
                .thenReturn(response);

        ApiResponse<DrinkResponse> apiResponse = new ApiResponse<>(
                true, Messages.DRINK_UPDATED, "test-trace-id", response, null
        );

        when(responseFactory.success(anyString(), any(DrinkResponse.class)))
                .thenReturn(apiResponse);

        // When & Then
        mockMvc.perform(put("/api/drinks/{id}", drinkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.DRINK_UPDATED));

        verify(drinkService).updateDrink(eq(drinkId), any(DrinkRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when update request is invalid")
    void shouldReturnBadRequestWhenUpdateRequestIsInvalid() throws Exception {
        // Given
        DrinkRequest request = new DrinkRequest();

        // When & Then
        mockMvc.perform(put("/api/drinks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(drinkService, never()).updateDrink(anyLong(), any());
    }

    @Test
    @DisplayName("Should return 404 when drink not found during update")
    void shouldReturnNotFoundWhenDrinkNotFoundDuringUpdate() throws Exception {
        // Given
        Long drinkId = 999L;
        DrinkRequest request = DrinkTestDataBuilder.aDrink().buildRequest();

        when(drinkService.updateDrink(eq(drinkId), any(DrinkRequest.class)))
                .thenThrow(new ResourceNotFoundException(Messages.DRINK_NOT_FOUND));

        ApiErrorResponse errorResponse = new ApiErrorResponse();
        when(responseFactory.error(HttpStatus.NOT_FOUND, Messages.DRINK_NOT_FOUND))
                .thenReturn(errorResponse);

        // When & Then
        mockMvc.perform(put("/api/drinks/{id}", drinkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(drinkService).updateDrink(eq(drinkId), any(DrinkRequest.class));
    }

    @Test
    @DisplayName("Should return 409 when drink already exists during update")
    void shouldReturnConflictWhenDrinkAlreadyExistsDuringUpdate() throws Exception {
        // Given
        Long drinkId = 1L;
        DrinkRequest request = DrinkTestDataBuilder.aDrink().buildRequest();

        when(drinkService.updateDrink(eq(drinkId), any(DrinkRequest.class)))
                .thenThrow(new ConflictException(Messages.DRINK_ALREADY_EXISTS));

        ApiErrorResponse errorResponse = new ApiErrorResponse();
        when(responseFactory.error(HttpStatus.CONFLICT, Messages.DRINK_ALREADY_EXISTS))
                .thenReturn(errorResponse);

        // When & Then
        mockMvc.perform(put("/api/drinks/{id}", drinkId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(drinkService).updateDrink(eq(drinkId), any(DrinkRequest.class));
    }

    // ============================================================
    // DELETE /api/drinks/{id}
    // ============================================================

    @Test
    @DisplayName("Should delete drink successfully")
    void shouldDeleteDrinkSuccessfully() throws Exception {
        // Given
        Long drinkId = 1L;

        doNothing().when(drinkService).deleteDrinkById(drinkId);

        ApiResponse<Void> apiResponse = new ApiResponse<>(
                true, Messages.DRINK_DELETED, "test-trace-id", null, null
        );

        when(responseFactory.<Void>success(anyString(), isNull()))
                .thenReturn(apiResponse);

        // When & Then
        mockMvc.perform(delete("/api/drinks/{id}", drinkId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.DRINK_DELETED));

        verify(drinkService).deleteDrinkById(drinkId);
    }

    @Test
    @DisplayName("Should return 404 when drink not found during delete")
    void shouldReturnNotFoundWhenDrinkNotFoundDuringDelete() throws Exception {
        // Given
        Long drinkId = 999L;

        doThrow(new ResourceNotFoundException(Messages.DRINK_NOT_FOUND))
                .when(drinkService).deleteDrinkById(drinkId);

        ApiErrorResponse errorResponse = new ApiErrorResponse();
        when(responseFactory.error(HttpStatus.NOT_FOUND, Messages.DRINK_NOT_FOUND))
                .thenReturn(errorResponse);

        // When & Then
        mockMvc.perform(delete("/api/drinks/{id}", drinkId))
                .andExpect(status().isNotFound());

        verify(drinkService).deleteDrinkById(drinkId);
    }
}