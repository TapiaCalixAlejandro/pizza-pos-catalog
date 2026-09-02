package com.msvc.catalog.controller;

import com.msvc.catalog.dto.ingredient.request.IngredientRequest;
import com.msvc.catalog.dto.ingredient.response.IngredientResponse;
import com.msvc.catalog.service.ingredient.IngredientService;
import com.msvc.catalog.shared.constans.Messages;
import com.msvc.catalog.shared.responses.ApiResponse;
import com.msvc.catalog.shared.responses.ResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ingredient")
@Tag(
        name = "Ingredients",
        description = "Operations related to ingredient management."
)
public class IngredientController {

    private final ResponseFactory responseFactory;
    private final IngredientService ingredientService;

    public IngredientController(
            ResponseFactory responseFactory,
            IngredientService ingredientService
    ) {
        this.responseFactory = responseFactory;
        this.ingredientService = ingredientService;
    }

    @Operation(
            summary = "Get all ingredients.",
            description = "Returns all ingredients available in the catalog."
    )
    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Ingredients retrieved successfully."
            )
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<IngredientResponse>>> findAll() {
        List<IngredientResponse> ingredients = ingredientService.findAllIngredient();

        return ResponseEntity.ok(
                responseFactory.success(
                        Messages.INGREDIENTS_RETRIEVED,
                        ingredients
                )
        );
    }

    @Operation(
            summary = "Create a new ingredient.",
            description = "Creates a new ingredient in the inventory."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Ingredient created successfully."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Ingredient already exists."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error."
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<IngredientResponse>> create(
            @Valid @RequestBody IngredientRequest request
    ) {
        IngredientResponse response = ingredientService.createIngredient(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        responseFactory.success(
                                Messages.INGREDIENT_CREATED,
                                response
                        )
                );
    }

    @Operation(
            summary = "Get ingredient by id.",
            description = "Returns a ingredient by its identifier."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Ingredient found successfully."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Ingredient not found."
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IngredientResponse>> findById(
            @PathVariable Long id
    ) {
        IngredientResponse response = ingredientService.findByIdIngredient(id);

        return ResponseEntity.ok(
                responseFactory.success(
                        Messages.INGREDIENT_FOUND,
                        response
                )
        );
    }

    @Operation(
            summary = "Update ingredient by id.",
            description = "Updates an existing ingredient in the catalog."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Ingredient update successfully."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Ingredient not found."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Ingredient already exists."
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IngredientResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody IngredientRequest request
    ) {
        IngredientResponse response = ingredientService.updateIngredient(id, request);

        return ResponseEntity.ok(
                responseFactory.success(
                        Messages.INGREDIENT_UPDATED,
                        response
                )
        );
    }

    @Operation(
            summary = "Delete ingredient by id",
            description = "Soft deleted a ingredient by its identifier."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Ingredient deleted successfully."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Ingredient not found."
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {
        ingredientService.deleteIngredient(id);

        return ResponseEntity.ok(
                responseFactory.success(
                        Messages.INGREDIENT_DELETED,
                        null
                )
        );
    }

}
