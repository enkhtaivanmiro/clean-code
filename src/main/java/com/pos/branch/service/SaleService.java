package com.pos.branch.service;

import com.pos.branch.dto.SaleRequest;
import com.pos.branch.dto.SaleResponse;
import com.pos.branch.model.Sale;
import java.util.UUID;

public interface SaleService {
    SaleResponse createSale(SaleRequest request);
    Sale getSaleById(UUID id);
}
