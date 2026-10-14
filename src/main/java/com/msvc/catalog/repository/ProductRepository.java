package com.msvc.catalog.repository;

import com.msvc.catalog.entity.Product;
import com.msvc.catalog.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndDeletedAtIsNull(Long id);

    Optional<Product> findByNameAndDeletedAtIsNull(String name);

    List<Product> findAllByDeletedAtIsNull();

    boolean existsByNameAndDeletedAtIsNull(String name);

    List<Product> findByProductTypeAndDeletedAtIsNull(ProductType productType);

}
