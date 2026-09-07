package com.msvc.catalog.repository;

import com.msvc.catalog.entity.Drink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DrinkRepository extends JpaRepository<Drink, Long> {

    Optional<Drink> findByIdAndDeletedAtIsNull(Long id);

    Optional<Drink> findByProductIdAndDeletedAtIsNull(Long productId);

    List<Drink> findAllByDeletedAtIsNull();

    boolean existsByProductIdAndDeletedAtIsNull(Long productId);

}
