package com.msvc.catalog.service.drink;

import com.msvc.catalog.dto.drink.request.DrinkRequest;
import com.msvc.catalog.dto.drink.response.DrinkResponse;
import com.msvc.catalog.entity.Drink;
import com.msvc.catalog.entity.Product;
import com.msvc.catalog.enums.ProductType;
import com.msvc.catalog.mapper.DrinkMapper;
import com.msvc.catalog.repository.DrinkRepository;
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
public class DrinkServiceImpl implements DrinkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DrinkServiceImpl.class);

    private final DrinkMapper drinkMapper;
    private final DrinkRepository drinkRepository;
    private final ProductRepository productRepository;

    public DrinkServiceImpl(
            DrinkMapper drinkMapper,
            DrinkRepository drinkRepository,
            ProductRepository productRepository
    ) {
        this.drinkMapper = drinkMapper;
        this.drinkRepository = drinkRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public DrinkResponse createDrink(DrinkRequest request) {
        LOGGER.info(
                "Creating drink [productId={}]",
                request.getProductId()
        );

        Product product = productRepository
                .findByIdAndDeletedAtIsNull(request.getProductId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.PRODUCT_NOT_FOUND)
                );

        if (product.getProductType() != ProductType.DRINK) {
            throw new BusinessException(Messages.PRODUCT_MUST_BE_DRINK);
        }

        if (drinkRepository.existsByProductIdAndDeletedAtIsNull(request.getProductId())) {
            throw new BusinessException(Messages.DRINK_ALREADY_EXISTS);
        }

        Drink drink = new Drink();

        drink.setProduct(product);
        drink.setVolume(request.getVolume());
        drink.setAlcoholic(request.getAlcoholic());

        Drink savedDrink = drinkRepository.save(drink);

        LOGGER.info(
                "Drink created successfully [id={}, productId={}]",
                savedDrink.getId(),
                product.getId()
        );

        return drinkMapper.toResponse(savedDrink);
    }

    @Override
    @Transactional(readOnly = true)
    public DrinkResponse getDrinkById(Long id) {
        LOGGER.info(
                "Getting drink [id={}]",
                id
        );

        Drink drink = drinkRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.DRINK_NOT_FOUND)
                );

        return drinkMapper.toResponse(drink);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DrinkResponse> getAllDrinks() {
        LOGGER.info("Getting all drinks");

        List<Drink> drinks = drinkRepository.findAllByDeletedAtIsNull();

        return drinkMapper.toResponseList(drinks);
    }

    @Override
    @Transactional
    public DrinkResponse updateDrink(Long id, DrinkRequest request) {
        LOGGER.info(
                "Updating drink [id={}]",
                id
        );

        Drink drink = drinkRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.DRINK_NOT_FOUND)
                );

        Product product = productRepository
                .findByIdAndDeletedAtIsNull(request.getProductId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.PRODUCT_NOT_FOUND)
                );

        if (product.getProductType() != ProductType.DRINK) {
            throw new BusinessException(Messages.PRODUCT_MUST_BE_DRINK);
        }

        if (!drink.getProduct().getId().equals(request.getProductId())
                && drinkRepository.existsByProductIdAndDeletedAtIsNull(request.getProductId())) {
            throw new BusinessException(Messages.DRINK_ALREADY_EXISTS);
        }

        drink.setProduct(product);
        drink.setVolume(request.getVolume());

        Drink savedDrink = drinkRepository.save(drink);

        LOGGER.info(
                "Drink updated successfully [id={}, productId={}]",
                savedDrink.getId(),
                product.getId()
        );

        return drinkMapper.toResponse(savedDrink);
    }

    @Override
    @Transactional
    public void deleteDrinkById(Long id) {
        LOGGER.info(
                "Deleting drink [id={}]",
                id
        );

        Drink drink = drinkRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.DRINK_NOT_FOUND)
                );

        drink.setDeletedAt(LocalDateTime.now());

        drinkRepository.save(drink);

        LOGGER.info(
                "Drink deleted successfully [id={}]",
                id
        );
    }

}
