package com.pos.branch.repository;

import com.pos.branch.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, UUID> {
    List<Sale> findByBranchIdAndSyncedFalse(Integer branchId);
    List<Sale> findBySyncedFalse();
    void deleteByCreatedAtBefore(java.time.LocalDateTime date);
}
