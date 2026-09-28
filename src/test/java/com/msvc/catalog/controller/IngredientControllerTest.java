package com.msvc.catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msvc.catalog.dto.ingredient.request.IngredientRequest;
import com.msvc.catalog.dto.ingredient.response.IngredientResponse;
import com.msvc.catalog.entity.Ingredient;
import com.msvc.catalog.enums.IngredientUnit;
import com.msvc.catalog.service.ingredient.IngredientService;
import com.msvc.catalog.shared.constans.Messages;
import com.msvc.catalog.shared.exception.BusinessException;
import com.msvc.catalog.shared.exception.ConflictException;
import com.msvc.catalog.shared.exception.ResourceNotFoundException;
import com.msvc.catalog.shared.responses.ApiErrorResponse;
import com.msvc.catalog.shared.responses.ApiResponse;
import com.msvc.catalog.shared.responses.ResponseFactory;
import com.msvc.catalog.support.IngredientTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebMvcTest(IngredientController.class)
public class IngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private IngredientService ingredientService;

    @MockitoBean
    private ResponseFactory responseFactory;

    @Test
    @DisplayName("")
    void shouldGetAllIngredientsSuccessfully() throws Exception {
        IngredientResponse response = IngredientTestDataBuilder.anIngredient().buildResponse();
        response.setId(1L);

        ApiResponse<List<IngredientResponse>> apiResponse =
                new ApiResponse<>(
                        true,
                        Messages.INGREDIENTS_RETRIEVED,
                        "test-trace-id",
                        List.of(response),
                        null
                );

        when(ingredientService.findAllIngredient())
                .thenReturn(List.of(response));
        when(responseFactory.<List<IngredientResponse>>success(anyString(), anyList()))
                .thenReturn(apiResponse);

        mockMvc
                .perform(
                        get("/api/ingredient")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.INGREDIENTS_RETRIEVED))
                .andExpect(jsonPath("$.traceId").value("test-trace-id"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Mozzarella"))
                .andExpect(jsonPath("$.data[0].unit").value("KILOGRAM"))
                .andExpect(jsonPath("$.data[0].stock").value(20))
                .andExpect(jsonPath("$.data[0].minimumStock").value(5))
                .andExpect(jsonPath("$.data[0].cost").value(180.00))
                .andExpect(jsonPath("$.data[0].status").value("ACTIVE"));

        verify(ingredientService).findAllIngredient();
        verify(responseFactory).success(anyString(), anyList());
    }

    @Test
    @DisplayName("")
    void shouldReturnEmptyListWhenThereAreNoIngredients() throws Exception {
        ApiResponse<List<IngredientResponse>> apiResponse =
                new ApiResponse<>(
                        true,
                        Messages.INGREDIENTS_RETRIEVED,
                        "test-trace-id",
                        List.of(),
                        null
                );


        when(ingredientService.findAllIngredient())
                .thenReturn(List.of());
        when(responseFactory.<List<IngredientResponse>>success(
                anyString(),
                anyList()
        )).thenReturn(apiResponse);

        mockMvc
                .perform(
                        get("/api/ingredient")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(ingredientService).findAllIngredient();
        verify(responseFactory).success(anyString(), anyList());
    }

    @Test
    @DisplayName("Should Create Ingredient Successfully")
    void shouldCreateIngredientSuccessfully() throws Exception {
        IngredientRequest request = IngredientTestDataBuilder.anIngredient().buildRequest();
        IngredientResponse response = IngredientTestDataBuilder.anIngredient().buildResponse();
        response.setId(1L);

        ApiResponse<IngredientResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        Messages.INGREDIENT_CREATED,
                        "test-trace-id",
                        response,
                        null
                );

        when(ingredientService.createIngredient(any(IngredientRequest.class)))
                .thenReturn(response);
        when(responseFactory.success(
                eq(Messages.INGREDIENT_CREATED),
                eq(response)
        )).thenReturn(apiResponse);

        mockMvc
                .perform(
                        post("/api/ingredient")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.INGREDIENT_CREATED))
                .andExpect(jsonPath("$.traceId").value("test-trace-id"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Mozzarella"))
                .andExpect(jsonPath("$.data.cost").value(180.00));

        verify(ingredientService).createIngredient(any(IngredientRequest.class));
        verify(responseFactory).success(eq(Messages.INGREDIENT_CREATED), eq(response));
    }

    @Test
    @DisplayName("Should return 400 when ingredient name is missing")
    void shouldReturnBadRequestWhenIngredientNameIsMissing() throws Exception {
        IngredientRequest request = IngredientTestDataBuilder.anIngredient().buildRequest();
        request.setName("");
        request.setUnit(null);
        request.setStock(new BigDecimal("0"));
        request.setMinimumStock(new BigDecimal("0"));
        request.setCost(new BigDecimal("0"));

        mockMvc
                .perform(
                        post("/api/ingredient")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(ingredientService);
    }

    @Test
    @DisplayName("Should return 409 when ingredient already exists")
    void shouldReturnBadRequestWhenIngredientAlreadyExists() throws Exception {
        IngredientRequest request = IngredientTestDataBuilder.anIngredient().buildRequest();

        when(ingredientService.createIngredient(any(IngredientRequest.class)))
                .thenThrow(new ConflictException(Messages.INGREDIENT_ALREADY_EXISTS));

        mockMvc
                .perform(
                        post("/api/ingredient")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict());

        verify(ingredientService).createIngredient(any(IngredientRequest.class));
    }

    @Test
    @DisplayName("Should return ingredient by id successfully")
    void shouldReturnIngredientByIdSuccessfully() throws Exception {
        IngredientResponse response = IngredientTestDataBuilder.anIngredient().buildResponse();
        response.setId(1L);

        ApiResponse<IngredientResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        Messages.INGREDIENT_FOUND,
                        "test-trace-id",
                        response,
                        null
                );

        when(ingredientService.findByIdIngredient(1L))
                .thenReturn(response);
        when(responseFactory.success(eq(Messages.INGREDIENT_FOUND), eq(response)))
                .thenReturn(apiResponse);

        mockMvc
                .perform(
                        get("/api/ingredient/{id}", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.INGREDIENT_FOUND))
                .andExpect(jsonPath("$.traceId").value("test-trace-id"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Mozzarella"))
                .andExpect(jsonPath("$.data.unit").value("KILOGRAM"))
                .andExpect(jsonPath("$.data.stock").value(20))
                .andExpect(jsonPath("$.data.minimumStock").value(5))
                .andExpect(jsonPath("$.data.cost").value(180.00))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        verify(ingredientService).findByIdIngredient(1L);
        verify(responseFactory).success(eq(Messages.INGREDIENT_FOUND), eq(response));
    }

    @Test
    @DisplayName("Should return 404 when ingredient does not exist")
    void shouldReturnNotFoundWhenIngredientDoesNotExist() throws Exception {
        ApiErrorResponse errorResponse = new ApiErrorResponse();

        when(ingredientService.findByIdIngredient(999L))
                .thenThrow(new ResourceNotFoundException(Messages.INGREDIENT_NOT_FOUND));
        when(responseFactory.error(HttpStatus.NOT_FOUND, Messages.INGREDIENT_NOT_FOUND))
                .thenReturn(errorResponse);

        mockMvc
                .perform(
                        get("/api/ingredient/{id}", 999L)
                )
                .andExpect(status().isNotFound());

        verify(ingredientService).findByIdIngredient(999L);
        verify(responseFactory).error(HttpStatus.NOT_FOUND, Messages.INGREDIENT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should update ingredient successfully")
    void shouldUpdateIngredientSuccessfully() throws Exception {
        IngredientRequest request = IngredientTestDataBuilder.anIngredient().buildRequest();
        request.setName("Mass");
        IngredientResponse response = IngredientTestDataBuilder.anIngredient().buildResponse();
        response.setId(1L);
        response.setName("Mass");

        ApiResponse<IngredientResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        Messages.INGREDIENT_UPDATED,
                        "test-trace-id",
                        response,
                        null
                );

        when(ingredientService.updateIngredient(eq(1L), any(IngredientRequest.class)))
                .thenReturn(response);
        when(responseFactory.success(anyString(), any(IngredientResponse.class)))
                .thenReturn(apiResponse);

        mockMvc
                .perform(
                        put("/api/ingredient/{id}", 1L)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.INGREDIENT_UPDATED))
                .andExpect(jsonPath("$.traceId").value("test-trace-id"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Mass"))
                .andExpect(jsonPath("$.data.stock").value(20))
                .andExpect(jsonPath("$.data.minimumStock").value(5))
                .andExpect(jsonPath("$.data.cost").value(180.00));

        verify(ingredientService).updateIngredient(eq(1L), any(IngredientRequest.class));
        verify(responseFactory).success(anyString(), any(IngredientResponse.class));
    }

    @Test
    @DisplayName("Should return 400 when update request is invalid")
    void shouldReturnBadRequestWhenUpdateRequestIsInvalid() throws Exception {
        IngredientRequest request = IngredientTestDataBuilder.anIngredient().buildRequest();
        request.setName("");
        request.setUnit(null);
        request.setStock(BigDecimal.ZERO);
        request.setMinimumStock(BigDecimal.ZERO);
        request.setCost(BigDecimal.ZERO);

        mockMvc
                .perform(
                        put("/api/ingredient/{id}", 1L)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(ingredientService);
    }


    @Test
    @DisplayName("Should return 404 when ingredient does not exist during update")
    void shouldReturnNotFoundWhenIngredientDoesNotExistDuringUpdate() throws Exception {
        IngredientRequest request = IngredientTestDataBuilder.anIngredient().buildRequest();

        ApiErrorResponse errorResponse = new ApiErrorResponse();

        when(ingredientService.updateIngredient(eq(1L), any(IngredientRequest.class)))
                .thenThrow(new ResourceNotFoundException(Messages.INGREDIENT_NOT_FOUND));
        when(responseFactory.error(HttpStatus.NOT_FOUND, Messages.INGREDIENT_NOT_FOUND))
                .thenReturn(errorResponse);

        mockMvc
                .perform(
                        put("/api/ingredient/{id}", 1L)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound());

        verify(ingredientService).updateIngredient(eq(1L), any(IngredientRequest.class));
        verify(responseFactory).error(HttpStatus.NOT_FOUND, Messages.INGREDIENT_NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 409 when ingredient name already exists")
    void shouldReturnConflictWhenIngredientNameAlreadyExists() throws Exception {
        IngredientRequest request = IngredientTestDataBuilder.anIngredient().buildRequest();

        ApiErrorResponse errorResponse = new ApiErrorResponse();

        when(ingredientService.updateIngredient(eq(1L), any(IngredientRequest.class)))
                .thenThrow(new ConflictException(Messages.INGREDIENT_ALREADY_EXISTS));
        when(responseFactory.error(HttpStatus.CONFLICT, Messages.INGREDIENT_ALREADY_EXISTS))
                .thenReturn(errorResponse);

        mockMvc
                .perform(
                        put("/api/ingredient/{id}", 1L)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict());

        verify(ingredientService).updateIngredient(eq(1L), any(IngredientRequest.class));
        verify(responseFactory).error(HttpStatus.CONFLICT, Messages.INGREDIENT_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("Should soft delete ingredient successfully")
    void shouldSoftDeleteIngredientSuccessfully() throws Exception {
        Ingredient ingredient = IngredientTestDataBuilder.anIngredient().build();

        ApiResponse<Void> apiResponse =
                new ApiResponse<>(
                        true,
                        Messages.INGREDIENT_DELETED,
                        "test-trace-id",
                        null,
                        null
                );

        doNothing()
                .when(ingredientService)
                .deleteIngredient(1L);
        when(responseFactory.<Void>success(
                anyString(),
                isNull()
        )).thenReturn(apiResponse);

        mockMvc
                .perform(
                        delete("/api/ingredient/{id}", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(Messages.INGREDIENT_DELETED))
                .andExpect(jsonPath("$.traceId").value("test-trace-id"));

        verify(ingredientService).deleteIngredient(1L);
        verify(responseFactory).success(anyString(),isNull());
    }

    @Test
    @DisplayName("Should return 404 when ingredient does not exist")
    void shouldReturnNotFoundIngredientDoesNotExist() throws Exception {
        ResourceNotFoundException exception =
                new ResourceNotFoundException(Messages.INGREDIENT_NOT_FOUND);

        ApiErrorResponse errorResponse = new ApiErrorResponse();

        doThrow(exception)
                .when(ingredientService)
                .deleteIngredient(1L);
        when(responseFactory.error(HttpStatus.NOT_FOUND, Messages.INGREDIENT_NOT_FOUND))
                .thenReturn(errorResponse);

        mockMvc
                .perform(
                        delete("/api/ingredient/{id}", 1L)
                )
                .andExpect(status().isNotFound());

        verify(ingredientService).deleteIngredient(1L);
        verify(responseFactory).error(HttpStatus.NOT_FOUND, Messages.INGREDIENT_NOT_FOUND);
    }

}
