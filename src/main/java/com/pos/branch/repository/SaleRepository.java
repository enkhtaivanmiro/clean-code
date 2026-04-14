package com.pos.branch.repository;

import com.pos.branch.model.Sale;
import com.pos.branch.model.SaleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, SaleId> {
    @Query("SELECT s FROM Sale s WHERE s.id = :uuid")
    Optional<Sale> findByUuid(@Param("uuid") UUID uuid);

    List<Sale> findByBranchIdAndSyncedFalse(Integer branchId);
    List<Sale> findBySyncedFalse();
    void deleteByCreatedAtBefore(java.time.LocalDateTime date);
}
