package com.msvc.catalog.controller;

import com.msvc.catalog.dto.dessert.request.DessertRequest;
import com.msvc.catalog.dto.dessert.response.DessertResponse;
import com.msvc.catalog.service.dessert.DessertService;
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
@RequestMapping("/api/desserts")
@Tag(
        name = "Desserts",
        description = "Operation related to dessert management."
)
public class DessertController {

    private final ResponseFactory responseFactory;
    private final DessertService dessertService;

    public DessertController(
            ResponseFactory responseFactory,
            DessertService dessertService
    ) {
        this.responseFactory = responseFactory;
        this.dessertService = dessertService;
    }
    @Operation(
            summary = "Create a new dessert",
            description = "Creates a new dessert configuration for an existing dessert."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Dessert created successfully."
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
                    description = "Dessert already exists."
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<DessertResponse>> create(
            @Valid @RequestBody DessertRequest request
    ) {
        DessertResponse response = dessertService.createDessert(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        responseFactory.success(
                                Messages.DESSERT_CREATED,
                                response
                        )
                );
    }

    @Operation(
            summary = "Get dessert by id",
            description = "Returns a dessert by its identifier."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Dessert found successfully."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Dessert not found."
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DessertResponse>> findById(
            @PathVariable Long id
    ) {
        DessertResponse response = dessertService.findDessertById(id);

        return ResponseEntity
                .ok(
                        responseFactory
                                .success(
                                        Messages.DESSERT_FOUND,
                                        response
                                )
                );
    }

    @Operation(
            summary = "Get all desserts",
            description = "Returns all desserts available in the catalog."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Desserts retrieved successfully."
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<DessertResponse>>> findAll() {
        List<DessertResponse> responses = dessertService.findAllDesserts();

        return ResponseEntity
                .ok(
                      responseFactory
                              .success(
                                      Messages.DESSERT_RETRIEVED,
                                      responses
                              )
                );
    }

    @Operation(
            summary = "Updated dessert by id",
            description = "Updates an existing dessert in the catalog."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Dessert updated successfully."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Dessert not found."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Dessert already exists."
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DessertResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody DessertRequest request
    ) {
        DessertResponse response = dessertService.updateDessert(id, request);

        return ResponseEntity
                .ok(
                        responseFactory
                                .success(
                                        Messages.DESSERT_UPDATED,
                                        response
                                )
                );
    }

    @Operation(
            summary = "Delete dessert by id",
            description = "Soft delete a dessert by its identifier."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Dessert deleted successfully."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Dessert not found."
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {
        dessertService.deleteDessertById(id);

        return ResponseEntity
                .ok(
                        responseFactory
                                .success(
                                        Messages.DESSERT_DELETED,
                                        null
                                )
                );
    }

}
