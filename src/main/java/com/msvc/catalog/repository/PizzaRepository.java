package com.msvc.catalog.repository;

import com.msvc.catalog.entity.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PizzaRepository extends JpaRepository<Pizza, Long> {

    Optional<Pizza> findByIdAndDeletedAtIsNull(Long id);

    Optional<Pizza> findByProductIdAndDeletedAtIsNull(Long productId);

    List<Pizza> findAllByDeletedAtIsNull();

    boolean existsByProductIdAndDeletedAtIsNull(Long productId);

}
