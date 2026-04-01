package com.pos.branch.repository;

import com.pos.branch.model.POSTerminal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface POSTerminalRepository extends JpaRepository<POSTerminal, Integer> {
}
