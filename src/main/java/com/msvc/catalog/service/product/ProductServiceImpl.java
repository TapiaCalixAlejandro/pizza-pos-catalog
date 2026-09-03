package com.msvc.catalog.service.product;

import com.msvc.catalog.dto.product.request.ProductRequest;
import com.msvc.catalog.dto.product.response.ProductResponse;
import com.msvc.catalog.entity.Product;
import com.msvc.catalog.mapper.ProductMapper;
import com.msvc.catalog.repository.ProductRepository;
import com.msvc.catalog.shared.constans.Messages;
import com.msvc.catalog.shared.exception.BusinessException;
import com.msvc.catalog.shared.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    public ProductServiceImpl(
            ProductMapper productMapper,
            ProductRepository productRepository
    ) {
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        LOGGER.info(
                "Creating product [name={}, type={}, price={}]",
                request.getName(),
                request.getProductType(),
                request.getPrice()
        );

        if (productRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new BusinessException(Messages.PRODUCT_ALREADY_EXISTS);
        }

        Product product = productMapper.toEntity(request);

        Product saved = productRepository.save(product);

        LOGGER.info("Product created successfully [id={}]", saved.getId());

        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getByIdProduct(Long id) {
        Product product = productRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> {
                            LOGGER.warn("Product not found [id={}]", id);
                            return new ResourceNotFoundException(Messages.PRODUCT_NOT_FOUND);
                        }
                );

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        LOGGER.info("Getting all products.");

        return productRepository
                .findAllByDeletedAtIsNull()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        LOGGER.info(
                "Update product [name={}, type={}, price={}]",
                request.getName(),
                request.getProductType(),
                request.getPrice()
        );

        Product product = productRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.PRODUCT_NOT_FOUND)
                );

        if (!product.getName().equalsIgnoreCase(request.getName())
                && productRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new BusinessException(Messages.PRODUCT_ALREADY_EXISTS);
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImage(request.getImage());
        product.setProductType(request.getProductType());

        Product updated = productRepository.save(product);

        LOGGER.info(
                "Product updated successfully [id={}, name={}]",
                updated.getId(),
                updated.getName()
        );

        return productMapper.toResponse(updated);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Product not found.")
                );

        product.setDeletedAt(LocalDateTime.now());

        productRepository.save(product);

        LOGGER.info("Product soft deleted [id={}]", id);
    }
}
