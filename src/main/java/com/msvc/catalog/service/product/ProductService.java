package com.msvc.catalog.service.product;

import com.msvc.catalog.dto.product.request.ProductRequest;
import com.msvc.catalog.dto.product.response.ProductResponse;
import com.msvc.catalog.service.storage.StoredImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request, MultipartFile image);

    ProductResponse getByIdProduct(Long id);

    List<ProductResponse> getAllProducts();

    ProductResponse updateProduct(
            Long id,
            ProductRequest request,
            MultipartFile image
    );

    void deleteProduct(Long id);

    StoredImage getProductImage(Long id);

}
