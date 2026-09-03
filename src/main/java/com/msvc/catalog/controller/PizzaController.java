package com.msvc.catalog.controller;

import com.msvc.catalog.dto.pizza.request.PizzaRequest;
import com.msvc.catalog.dto.pizza.response.PizzaResponse;
import com.msvc.catalog.service.pizza.PizzaService;
import com.msvc.catalog.shared.constans.Messages;
import com.msvc.catalog.shared.responses.ApiResponse;
import com.msvc.catalog.shared.responses.ResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pizzas")
@Tag(
        name = "Pizzas",
        description = "Operations related to pizza management."
)
public class PizzaController {

    private final ResponseFactory responseFactory;
    private final PizzaService pizzaService;

    public PizzaController(
            ResponseFactory responseFactory,
            PizzaService pizzaService
    ) {
        this.responseFactory = responseFactory;
        this.pizzaService = pizzaService;
    }

    @Operation(
            summary = "Create a pizza",
            description = "Creates a pizza configuration for an existing product."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Pizza created successfully."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Product or ingredient not found."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Pizza already exists."
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<PizzaResponse>> create(
            @Valid @RequestBody PizzaRequest request
    ) {
        PizzaResponse response = pizzaService.createPizza(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        responseFactory.success(
                                Messages.PIZZA_CREATED,
                                response
                        )
                );
    }

    @Operation(
            summary = "Get pizza by id",
            description = "Returns a pizza by its identifier."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Pizza found successfully."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Pizza not found."
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PizzaResponse>> findById(
            @PathVariable Long id
    ) {
        PizzaResponse response = pizzaService.getPizzaById(id);

        return ResponseEntity
                .ok(
                        responseFactory.success(
                                Messages.PIZZA_FOUND,
                                response
                        )
                );
    }

    @Operation(
            summary = "Get all pizzas",
            description = "Returns all pizzas available in the catalog."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Pizzas retrieved successfully."
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<PizzaResponse>>> getAll() {
        List<PizzaResponse> pizzaResponses = pizzaService.getAllPizzas();

        return ResponseEntity
                .ok(
                        responseFactory.success(
                                Messages.PIZZA_RETRIEVED,
                                pizzaResponses
                        )
                );
    }

}
