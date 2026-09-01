package com.msvc.catalog.service.product;

import com.msvc.catalog.dto.product.request.ProductRequest;
import com.msvc.catalog.dto.product.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getByIdProduct(Long id);

    List<ProductResponse> getAllProducts();

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);

}
