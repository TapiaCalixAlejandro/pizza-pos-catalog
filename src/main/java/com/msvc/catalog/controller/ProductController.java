package com.msvc.catalog.controller;

import com.msvc.catalog.dto.product.request.ProductRequest;
import com.msvc.catalog.dto.product.response.ProductResponse;
import com.msvc.catalog.service.product.ProductService;
import com.msvc.catalog.service.storage.StoredImage;
import com.msvc.catalog.shared.constans.Messages;
import com.msvc.catalog.shared.responses.ApiResponse;
import com.msvc.catalog.shared.responses.ResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(
        name = "Products",
        description = "Operations related to product management."
)
public class ProductController {

    private final ResponseFactory responseFactory;
    private final ProductService productService;

    public ProductController(
            ResponseFactory responseFactory,
            ProductService productService
    ) {
        this.responseFactory = responseFactory;
        this.productService = productService;
    }

    @Operation(
            summary = "Create a product",
            description = "Creates a new product in the catalog."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation error."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Product already exists."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error."
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestPart("product") ProductRequest request,
            @RequestPart("image")MultipartFile image
    ) {
        ProductResponse response = productService.createProduct(request, image);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        responseFactory.success(
                                Messages.PRODUCT_CREATED,
                                response
                        )
                );
    }

    @Operation(
            summary = "Get product by id",
            description = "Returns a product by its identifier."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Product found successfully."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Product not found."
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(
            @PathVariable Long id
    ) {
        ProductResponse response = productService.getByIdProduct(id);

        return ResponseEntity
                .ok(
                        responseFactory.success(
                                Messages.PRODUCT_FOUND,
                                response
                        )
                );
    }

    @Operation(
            summary = "Get all products",
            description = "Returns all products available in the catalog."
    )
    @ApiResponses(
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully."
            )
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findAll() {
        List<ProductResponse> responseList = productService.getAllProducts();

        return ResponseEntity
                .ok(
                        responseFactory
                                .success(
                                        Messages.PRODUCTS_RETRIEVED,
                                        responseList
                                )
                );
    }

    @Operation(
            summary = "Update product by id",
            description = "Updates an existing product in the catalog."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully."
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
                    description = "Product already exists."
            )
    })
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = {
                            @Encoding(
                                    name = "product",
                                    contentType = MediaType.APPLICATION_JSON_VALUE
                            )
                    }
            )
    )
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestPart("product") ProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        ProductResponse response = productService.updateProduct(id, request, image);

        return ResponseEntity
                .ok(
                        responseFactory.success(
                                Messages.PRODUCT_UPDATED,
                                response
                        )
                );
    }

    @Operation(
            summary = "Delete product by id",
            description = "Soft deletes a product by its identifier."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Product deleted successfully."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Product not found."
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {
        productService.deleteProduct(id);

        return ResponseEntity.ok(
                responseFactory.success(
                        Messages.PRODUCT_DELETED,
                        null
                )
        );
    }

    @Operation(
            summary = "Get product image",
            description = "Returns the image associated with a product."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Product image retrieved successfully."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Product or image not found."
            )
    })
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getProductImage(
            @PathVariable Long id
    ) {
        StoredImage image = productService.getProductImage(id);

        return ResponseEntity
                .ok()
                .contentType(
                        MediaType.parseMediaType(image.contentType())
                )
                .body(image.content());
    }
}
