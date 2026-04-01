package com.pos.branch.repository;

import com.pos.branch.model.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleItemRepository extends JpaRepository<SaleItem, Integer> {
}
