package com.pos.branch.repository;

import com.pos.branch.model.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductPriceRepository extends JpaRepository<ProductPrice, Integer> {
    @Query("SELECT pp FROM ProductPrice pp WHERE pp.product.id = :productId ORDER BY pp.updatedAt DESC LIMIT 1")
    Optional<ProductPrice> findLatestByProductId(@Param("productId") Integer productId);

    List<ProductPrice> findByUpdatedAtAfter(LocalDateTime since);
}
