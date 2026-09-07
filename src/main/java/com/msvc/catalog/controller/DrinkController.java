package com.msvc.catalog.controller;

import com.msvc.catalog.dto.drink.request.DrinkRequest;
import com.msvc.catalog.dto.drink.response.DrinkResponse;
import com.msvc.catalog.service.drink.DrinkService;
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
@RequestMapping("/api/drinks")
@Tag(
        name = "Drinks",
        description = "Operation related to drink management."
)
public class DrinkController {

    private final ResponseFactory responseFactory;
    private final DrinkService drinkService;

    public DrinkController(
            ResponseFactory responseFactory,
            DrinkService drinkService
    ) {
        this.responseFactory = responseFactory;
        this.drinkService = drinkService;
    }

    @Operation(
            summary = "Create a new drink.",
            description = "Creates a new drink configuration for an existing drink."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Drink created successfully."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Product not found."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Drink already exists."
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<DrinkResponse>> create(
            @Valid @RequestBody DrinkRequest request
    ) {
        DrinkResponse response = drinkService.createDrink(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        responseFactory.success(
                                Messages.DRINK_CREATED,
                                response
                        )
                );
    }

    @Operation(
            summary = "Get drink by id",
            description = "Returns a drink by its identifier."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Drink found successfully."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Drink not found."
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DrinkResponse>> findById(
            @PathVariable Long id
    ) {
        DrinkResponse response = drinkService.getDrinkById(id);

        return ResponseEntity
                .ok(
                        responseFactory.success(
                                Messages.DRINK_FOUND,
                                response
                        )
                );
    }

    @Operation(
            summary = "Get all drinks",
            description = "Returns all drinks available in the catalog."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Drinks retrieved successfully."
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<DrinkResponse>>> getAll() {
        List<DrinkResponse> responses = drinkService.getAllDrinks();

        return ResponseEntity
                .ok(
                        responseFactory.success(
                                Messages.DRINK_RETRIEVED,
                                responses
                        )
                );
    }

    @Operation(
            summary = "Updated drink by id.",
            description = "Updates an existing drink in the catalog."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Drink update successfully."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Drink not found."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Drink already exists."
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DrinkResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody DrinkRequest request
    ) {
        DrinkResponse response = drinkService.updateDrink(id, request);

        return ResponseEntity
                .ok(
                        responseFactory.success(
                                Messages.DRINK_UPDATED,
                                response
                        )
                );
    }

    @Operation(
            summary = "Delete drink by id.",
            description = "Soft delete a drink by its identifier."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Drink deleted successfully."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Drink not found."
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {
        drinkService.deleteDrinkById(id);

        return ResponseEntity
                .ok(
                        responseFactory.success(
                                Messages.DRINK_DELETED,
                                null
                        )
                );
    }

}
