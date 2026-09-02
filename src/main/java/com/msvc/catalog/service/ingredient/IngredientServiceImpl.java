package com.msvc.catalog.service.ingredient;

import com.msvc.catalog.dto.ingredient.request.IngredientRequest;
import com.msvc.catalog.dto.ingredient.response.IngredientResponse;
import com.msvc.catalog.entity.Ingredient;
import com.msvc.catalog.mapper.IngredientMapper;
import com.msvc.catalog.repository.IngredientRepository;
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
public class IngredientServiceImpl implements IngredientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IngredientServiceImpl.class);

    private final IngredientMapper ingredientMapper;
    private final IngredientRepository ingredientRepository;

    public IngredientServiceImpl(
            IngredientMapper ingredientMapper,
            IngredientRepository ingredientRepository
    ) {
        this.ingredientMapper = ingredientMapper;
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngredientResponse> findAllIngredient() {

        return ingredientRepository
                .findAllByDeletedAtIsNull()
                .stream()
                .map(ingredientMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public IngredientResponse createIngredient(IngredientRequest request) {
        LOGGER.info(
                "Creating ingredient [name={}].",
                request.getName()
        );

        if (ingredientRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new BusinessException(Messages.INGREDIENT_ALREADY_EXISTS);
        }

        Ingredient ingredient = ingredientMapper.toEntity(request);

        Ingredient savedIngredient = ingredientRepository.save(ingredient);

        LOGGER.info(
                "Ingredient created successfully [id={}].",
                savedIngredient.getId()
        );

        return ingredientMapper.toResponse(savedIngredient);
    }

    @Override
    @Transactional(readOnly = true)
    public IngredientResponse findByIdIngredient(Long id) {
        Ingredient ingredient = ingredientRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> {
                            LOGGER.warn("Ingredient not found. [id={}].", id);
                            return new ResourceNotFoundException(Messages.INGREDIENT_NOT_FOUND);
                        }
                );

        return ingredientMapper.toResponse(ingredient);
    }

    @Override
    @Transactional
    public IngredientResponse updateIngredient(Long id, IngredientRequest request) {
        LOGGER.info(
                "Update ingredient [name={}, unit={}, cost={}]",
                request.getName(),
                request.getUnit(),
                request.getCost()
        );

        Ingredient ingredient = ingredientRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.INGREDIENT_NOT_FOUND)
                );

        String name = request.getName().trim();

        if (!ingredient.getName().equalsIgnoreCase(name)
                && ingredientRepository.existsByNameAndDeletedAtIsNull(name)) {
            throw new BusinessException(Messages.INGREDIENT_ALREADY_EXISTS);
        }

        ingredient.setName(name);
        ingredient.setUnit(request.getUnit());
        ingredient.setStock(request.getStock());
        ingredient.setMinimumStock(request.getMinimumStock());
        ingredient.setCost(request.getCost());

        LOGGER.info(
                "Ingredient updated successfully [id={}, name={}]",
                ingredient.getId(),
                ingredient.getName()
        );

        return ingredientMapper.toResponse(ingredient);
    }

    @Override
    public void deleteIngredient(Long id) {
        Ingredient ingredient = ingredientRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.INGREDIENT_NOT_FOUND)
                );

        ingredient.setDeletedAt(LocalDateTime.now());

        ingredientRepository.save(ingredient);

        LOGGER.info("Ingredient soft deleted [id={}]", id);
    }
}
