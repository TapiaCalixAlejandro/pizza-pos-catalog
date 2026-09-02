package com.msvc.catalog.repository;

import com.msvc.catalog.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    Optional<Ingredient> findByIdAndDeletedAtIsNull(Long id);

    Optional<Ingredient> findByNameAndDeletedAtIsNull(String name);

    boolean existsByNameAndDeletedAtIsNull(String name);

    List<Ingredient> findAllByDeletedAtIsNull();

}
