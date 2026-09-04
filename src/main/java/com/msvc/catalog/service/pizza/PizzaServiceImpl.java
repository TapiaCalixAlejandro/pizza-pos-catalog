package com.msvc.catalog.service.pizza;

import com.msvc.catalog.dto.pizza.request.PizzaIngredientRequest;
import com.msvc.catalog.dto.pizza.request.PizzaRequest;
import com.msvc.catalog.dto.pizza.response.PizzaResponse;
import com.msvc.catalog.entity.Ingredient;
import com.msvc.catalog.entity.Pizza;
import com.msvc.catalog.entity.PizzaIngredient;
import com.msvc.catalog.entity.Product;
import com.msvc.catalog.enums.ProductType;
import com.msvc.catalog.mapper.PizzaIngredientMapper;
import com.msvc.catalog.mapper.PizzaMapper;
import com.msvc.catalog.repository.IngredientRepository;
import com.msvc.catalog.repository.PizzaRepository;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PizzaServiceImpl implements PizzaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PizzaServiceImpl.class);

    private final PizzaMapper pizzaMapper;
    private final PizzaIngredientMapper pizzaIngredientMapper;
    private final PizzaRepository pizzaRepository;
    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;

    public PizzaServiceImpl(
            PizzaMapper pizzaMapper,
            PizzaIngredientMapper pizzaIngredientMapper,
            PizzaRepository pizzaRepository,
            ProductRepository productRepository,
            IngredientRepository ingredientRepository
    ) {
        this.pizzaMapper = pizzaMapper;
        this.pizzaIngredientMapper = pizzaIngredientMapper;
        this.pizzaRepository = pizzaRepository;
        this.productRepository = productRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    @Transactional
    public PizzaResponse createPizza(PizzaRequest request) {
        LOGGER.info(
                "Creating pizza [productId={}, preparationTime={}]",
                request.getProductId(),
                request.getPreparationTime()
        );

        Product product = productRepository
                .findByIdAndDeletedAtIsNull(request.getProductId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.PRODUCT_NOT_FOUND)
                );

        if (product.getProductType() != ProductType.PIZZA) {
            throw new BusinessException(Messages.PRODUCT_MUST_BE_PIZZA);
        }

        if (pizzaRepository.existsByProductIdAndDeletedAtIsNull(request.getProductId())) {
            throw new BusinessException(Messages.PIZZA_ALREADY_EXISTS);
        }

        validateDuplicateIngredients(request);

        List<Long> ingredientIds = request
                .getIngredients()
                .stream()
                .map(PizzaIngredientRequest::getIngredientId)
                .toList();

        List<Ingredient> ingredients = ingredientRepository
                .findAllByIdInAndDeletedAtIsNull(ingredientIds);

        if (ingredients.size() != ingredientIds.size()) {
            throw new ResourceNotFoundException(Messages.INGREDIENT_NOT_FOUND);
        }

        Pizza pizza = new Pizza();

        pizza.setProduct(product);
        pizza.setPreparationTime(request.getPreparationTime());

        List<PizzaIngredient> pizzaIngredients = request
                .getIngredients()
                .stream()
                .map(item -> createPizzaIngredient(item, pizza, ingredients))
                .toList();

        pizza.setIngredients(pizzaIngredients);

        Pizza savedPizza = pizzaRepository.save(pizza);

        LOGGER.info(
                "Pizza created successfully [id={}, productId={}]",
                savedPizza.getId(),
                product.getId()
        );

        return pizzaMapper.toResponse(savedPizza);
    }

    @Override
    @Transactional(readOnly = true)
    public PizzaResponse getPizzaById(Long id) {
        LOGGER.info(
                "Getting pizza [id={}]",
                id
        );

        Pizza pizza = pizzaRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.PIZZA_NOT_FOUND)
                );

        return pizzaMapper.toResponse(pizza);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PizzaResponse> getAllPizzas() {
        LOGGER.info("Getting al pizzas");

        List<Pizza> pizzas = pizzaRepository.findAllByDeletedAtIsNull();

        return pizzaMapper.toResponseList(pizzas);
    }

    @Override
    @Transactional
    public PizzaResponse updatePizza(Long id, PizzaRequest request) {
        LOGGER.info(
                "Update pizza [id={}, productId={}, preparationTime={}]",
                id,
                request.getProductId(),
                request.getPreparationTime()
        );

        Pizza pizza = pizzaRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.PIZZA_NOT_FOUND)
                );

        Product product = productRepository
                .findByIdAndDeletedAtIsNull(request.getProductId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.PRODUCT_NOT_FOUND)
                );

        if (product.getProductType() != ProductType.PIZZA) {
            throw new BusinessException(Messages.PRODUCT_MUST_BE_PIZZA);
        }

        if (!pizza.getProduct().getId().equals(request.getProductId())
                && pizzaRepository.existsByProductIdAndDeletedAtIsNull(request.getProductId())) {
            throw new BusinessException(Messages.PIZZA_ALREADY_EXISTS);
        }

        validateDuplicateIngredients(request);

        List<Long> ingredientIds = request
                .getIngredients()
                .stream()
                .map(PizzaIngredientRequest::getIngredientId)
                .toList();

        List<Ingredient> ingredients = ingredientRepository
                .findAllByIdInAndDeletedAtIsNull(ingredientIds);

        if (ingredients.size() != ingredientIds.size()) {
            throw new ResourceNotFoundException(Messages.INGREDIENT_NOT_FOUND);
        }

        pizza.setProduct(product);
        pizza.setPreparationTime(request.getPreparationTime());

        List<PizzaIngredient> pizzaIngredients = request
                .getIngredients()
                .stream()
                .map(item -> createPizzaIngredient(item, pizza, ingredients))
                .toList();

        pizza.getIngredients().clear();

        pizzaRepository.flush();

        pizza.getIngredients().addAll(pizzaIngredients);

        Pizza updatePizza = pizzaRepository.save(pizza);

        LOGGER.info(
                "Pizza updated successfully [id={}, productId={}]",
                updatePizza.getId(),
                product.getId()
        );

        return pizzaMapper.toResponse(updatePizza);
    }

    @Override
    @Transactional
    public void deletePizzaById(Long id) {
        LOGGER.info(
                "Deleting pizza [id={}]",
                id
        );

        Pizza pizza = pizzaRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.PIZZA_NOT_FOUND)
                );

        pizza.setDeletedAt(LocalDateTime.now());

        pizza
                .getIngredients()
                .forEach(
                        ingredient -> ingredient.setDeletedAt(LocalDateTime.now())
                );

        pizzaRepository.save(pizza);

        LOGGER.info(
                "Pizza deleted successfully [id={}]",
                id
        );
    }

    private void validateDuplicateIngredients(PizzaRequest request) {
        if (request.getIngredients() == null || request.getIngredients().isEmpty()) {
            throw new BusinessException(Messages.PIZZA_MUST_HAVE_INGREDIENTS);
        }

        Set<Long> ingredientIds = request
                .getIngredients()
                .stream()
                .map(PizzaIngredientRequest::getIngredientId)
                .collect(Collectors.toSet());

        if (ingredientIds.size() != request.getIngredients().size()) {
            throw new BusinessException(Messages.INGREDIENT_DUPLICATE);
        }
    }

    private PizzaIngredient createPizzaIngredient(
            PizzaIngredientRequest request,
            Pizza pizza,
            List<Ingredient> ingredients
    ) {
        Ingredient ingredient = ingredients
                .stream()
                .filter(item -> item
                        .getId()
                        .equals(request.getIngredientId())
                )
                .findFirst()
                .orElseThrow(
                        () -> new ResourceNotFoundException(Messages.INGREDIENT_NOT_FOUND)
                );

        PizzaIngredient pizzaIngredient = pizzaIngredientMapper.toEntity(request);

        pizzaIngredient.setPizza(pizza);
        pizzaIngredient.setIngredient(ingredient);

        return pizzaIngredient;
    }
}
