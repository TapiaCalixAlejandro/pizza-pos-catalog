package com.msvc.catalog.service.product;

import com.msvc.catalog.dto.product.request.ProductRequest;
import com.msvc.catalog.dto.product.response.ProductResponse;
import com.msvc.catalog.entity.Product;
import com.msvc.catalog.mapper.ProductMapper;
import com.msvc.catalog.repository.ProductRepository;
import com.msvc.catalog.service.storage.ImageStorageService;
import com.msvc.catalog.service.storage.StoredImage;
import com.msvc.catalog.shared.constans.Messages;
import com.msvc.catalog.shared.exception.BusinessException;
import com.msvc.catalog.shared.exception.ResourceNotFoundException;
import com.msvc.catalog.shared.validation.ImageValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final Clock clock;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final ImageStorageService imageStorageService;
    private final ImageValidator imageValidator;

    public ProductServiceImpl(
            Clock clock,
            ProductMapper productMapper,
            ProductRepository productRepository,
            ImageStorageService imageStorageService,
            ImageValidator imageValidator
    ) {
        this.clock = clock;
        this.productMapper = productMapper;
        this.productRepository = productRepository;
        this.imageStorageService = imageStorageService;
        this.imageValidator = imageValidator;
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request, MultipartFile image) {
        LOGGER.info(
                "Creating product [type={}]",
                request.getProductType()
        );

        if (productRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new BusinessException(Messages.PRODUCT_ALREADY_EXISTS);
        }

        imageValidator.validate(image);

        String imageKey = null;

        try {

            imageKey = imageStorageService.store(image);
            Product product = productMapper.toEntity(request);
            product.setImage(imageKey);
            Product saved = productRepository.save(product);

            LOGGER.info(
                    "Product created successfully [id={}]",
                    saved.getId()
            );

            return productMapper.toResponse(saved);
        } catch (IOException exception) {
            if (imageKey != null) {
                try {
                    imageStorageService.delete(imageKey);
                } catch (IOException cleanupException) {
                    LOGGER.error(
                            "Could not clean up image after failure [imageKey={}]",
                            imageKey,
                            cleanupException
                    );
                }
            }

            throw  new BusinessException("Could not store product image.");
        }

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
    public ProductResponse updateProduct(
            Long id,
            ProductRequest request,
            MultipartFile image
    ) {
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

        String oldImageKey = product.getImage();
        String newImageKey = null;

        try {

            if (image != null && !image.isEmpty()) {
                imageValidator.validate(image);
                newImageKey = imageStorageService.store(image);
                product.setImage(newImageKey);
            }

            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setProductType(request.getProductType());

            Product updated = productRepository.save(product);

            if (newImageKey != null
                    && oldImageKey != null
                    && !oldImageKey.equals(newImageKey)) {
                imageStorageService.delete(oldImageKey);
            }

            LOGGER.info(
                    "Product updated successfully [id={}, name={}]",
                    updated.getId(),
                    updated.getName()
            );

            return productMapper.toResponse(updated);

        } catch (IOException exception) {

            if (newImageKey != null) {
                try {
                    imageStorageService.delete(newImageKey);
                } catch (IOException cleanupException) {
                    LOGGER.error(
                            "Could not clean up new image [imageKey={}]",
                            newImageKey,
                            cleanupException
                    );
                }
            }

            throw new BusinessException("Could not update product image");

        }

    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.PRODUCT_NOT_FOUND)
                );

        String imageKey = product.getImage();

        product.setDeletedAt(LocalDateTime.now(clock));

        productRepository.save(product);

        if (imageKey != null && !imageKey.isBlank()) {

            try {
                imageStorageService.delete(imageKey);
            } catch (IOException e) {
                LOGGER.error(
                        "Could not delete product image [productId={}, imageKey={}]",
                        id,
                        imageKey,
                        e
                );
            }
        }

        LOGGER.info(
                "Product soft deleted [id={}]",
                id
        );

    }

    @Override
    @Transactional(readOnly = true)
    public StoredImage getProductImage(Long id) {
        Product product = productRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.PRODUCT_NOT_FOUND)
                );

        if (product.getImage() == null
                || product.getImage().isBlank()) {
            throw new ResourceNotFoundException("Product image not found.");
        }

        try {
            return imageStorageService.load(product.getImage());
        } catch (IOException e) {
            LOGGER.error(
                    "Could not load product image [id={}]",
                    id,
                    e
            );

            throw new ResourceNotFoundException("Product image not found.");
        }
    }
}
