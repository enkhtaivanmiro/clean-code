package com.pos.branch.repository;

import com.pos.branch.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE p.name ILIKE %:query% AND (:category IS NULL OR p.category.name = :category)")
    Page<Product> searchProducts(@Param("query") String query, @Param("category") String category, Pageable pageable);

    java.util.Optional<Product> findFirstByNameContainingIgnoreCase(String name);
    
    @Query("SELECT p FROM Product p WHERE p.category.name ILIKE %:category%")
    java.util.Optional<Product> findFirstByCategoryName(@Param("category") String category);
}
