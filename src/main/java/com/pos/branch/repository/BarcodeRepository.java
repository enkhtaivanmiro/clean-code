package com.pos.branch.repository;

import com.pos.branch.model.Barcode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BarcodeRepository extends JpaRepository<Barcode, Integer> {
    Optional<Barcode> findByCode(String code);
}
