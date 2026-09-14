package com.msvc.catalog.service.dessert;

import com.msvc.catalog.dto.dessert.request.DessertRequest;
import com.msvc.catalog.dto.dessert.response.DessertResponse;
import com.msvc.catalog.entity.Dessert;
import com.msvc.catalog.entity.Product;
import com.msvc.catalog.enums.ProductType;
import com.msvc.catalog.mapper.DessertMapper;
import com.msvc.catalog.repository.DessertRepository;
import com.msvc.catalog.repository.ProductRepository;
import com.msvc.catalog.shared.constans.Messages;
import com.msvc.catalog.shared.exception.BusinessException;
import com.msvc.catalog.shared.exception.ConflictException;
import com.msvc.catalog.shared.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DessertServiceImpl implements DessertService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DessertServiceImpl.class);

    private final DessertMapper dessertMapper;
    private final DessertRepository dessertRepository;
    private final ProductRepository productRepository;

    public DessertServiceImpl(
            DessertMapper dessertMapper,
            DessertRepository dessertRepository,
            ProductRepository productRepository
    ) {
        this.dessertMapper = dessertMapper;
        this.dessertRepository = dessertRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public DessertResponse createDessert(DessertRequest request) {
        LOGGER.info(
                "Creating dessert [productId={}]",
                request.getProductId()
        );

        Product product = productRepository
                .findByIdAndDeletedAtIsNull(request.getProductId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.PRODUCT_NOT_FOUND)
                );

        if (product.getProductType() != ProductType.DESSERT) {
            throw new BusinessException(Messages.PRODUCT_MUST_BE_DESSERT);
        }

        if (dessertRepository.existsByProductIdAndDeletedAtIsNull(request.getProductId())) {
            throw new ConflictException(Messages.DESSERT_ALREADY_EXISTS);
        }

        Dessert dessert = new Dessert();

        dessert.setProduct(product);
        dessert.setPortion(request.getPortion());
        dessert.setPortionUnit(request.getPortionUnit());

        Dessert savedDessert = dessertRepository.save(dessert);

        LOGGER.info(
                "Dessert created successfully [id={}, productId={}]",
                savedDessert.getId(),
                product.getId()
        );

        return dessertMapper.toResponse(savedDessert);
    }

    @Override
    @Transactional(readOnly = true)
    public DessertResponse findDessertById(Long id) {
        LOGGER.info(
                "Getting dessert by id [id={}]",
                id
        );

        Dessert dessert = dessertRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.DESSERT_NOT_FOUND)
                );

        return dessertMapper.toResponse(dessert);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DessertResponse> findAllDesserts() {
        LOGGER.info(
                "Getting all desserts"
        );

        List<Dessert> desserts = dessertRepository
                .findAllByDeletedAtIsNull();

        return dessertMapper.toResponseList(desserts);
    }

    @Override
    @Transactional
    public DessertResponse updateDessert(Long id, DessertRequest request) {
        LOGGER.info(
                "Updating dessert [id={}, productId={}]",
                id,
                request.getProductId()
        );

        Dessert dessert = dessertRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.DESSERT_NOT_FOUND)
                );

        Product product = productRepository
                .findByIdAndDeletedAtIsNull(request.getProductId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.PRODUCT_NOT_FOUND)
                );

        if (product.getProductType() != ProductType.DESSERT) {
            throw new BusinessException(Messages.PRODUCT_MUST_BE_DESSERT);
        }

        if (!dessert.getProduct().getId().equals(request.getProductId())
            && dessertRepository.existsByProductIdAndDeletedAtIsNull(request.getProductId())) {
            throw new ConflictException(Messages.DESSERT_ALREADY_EXISTS);
        }

        dessert.setProduct(product);
        dessert.setPortion(request.getPortion());
        dessert.setPortionUnit(request.getPortionUnit());

        Dessert updateDessert = dessertRepository.save(dessert);

        LOGGER.info(
                "Dessert updated successfully [id={}, productId={}]",
                updateDessert.getId(),
                product.getId()
        );

        return dessertMapper.toResponse(updateDessert);
    }

    @Override
    @Transactional
    public void deleteDessertById(Long id) {
        LOGGER.info(
                "Deleting dessert [id={}]",
                id
        );

        Dessert dessert = dessertRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.DESSERT_NOT_FOUND)
                );

        dessert.setDeletedAt(LocalDateTime.now());

        dessertRepository.save(dessert);

        LOGGER.info(
                 "Dessert deleted successfully [id={}]",
                dessert.getId()
         );
    }
}
