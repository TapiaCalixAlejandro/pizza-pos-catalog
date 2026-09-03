package com.msvc.catalog.repository;

import com.msvc.catalog.entity.PizzaIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PizzaIngredientRepository extends JpaRepository<PizzaIngredient, Long> {

    List<PizzaIngredient> findAllByPizzaIdAndDeletedAtIsNull(Long pizzaId);

    boolean existsByPizzaIdAndIngredientIdAndDeletedAtIsNull(
            Long pizzaId,
            Long ingredientId
    );

}
