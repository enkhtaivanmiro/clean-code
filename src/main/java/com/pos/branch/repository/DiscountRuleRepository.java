package com.pos.branch.repository;

import com.pos.branch.model.DiscountRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRuleRepository extends JpaRepository<DiscountRule, Integer> {
}
