package com.pos.branch.repository;

import com.pos.branch.model.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.branch.id = :branchId AND i.product.id = :productId")
    Optional<Inventory> findWithLock(@Param("branchId") Integer branchId, @Param("productId") Integer productId);

    Optional<Inventory> findByBranchIdAndProductId(Integer branchId, Integer productId);
}
