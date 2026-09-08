package com.msvc.catalog.repository;

import com.msvc.catalog.entity.Dessert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DessertRepository extends JpaRepository<Dessert, Long> {

    Optional<Dessert> findByIdAndDeletedAtIsNull(Long id);

    Optional<Dessert> findByProductIdAndDeletedAtIsNull(Long productId);

    List<Dessert> findAllByDeletedAtIsNull();

    boolean existsByProductIdAndDeletedAtIsNull(Long productId);

}
