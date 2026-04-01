package com.pos.branch.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record SaleRequest(
    @NotNull UUID id,
    @NotNull @Positive Integer branch_id,
    @NotNull @Positive Integer pos_id,
    @NotNull @Positive Integer payment_type_id,
    @NotEmpty List<@jakarta.validation.Valid SaleItemRequest> items,
    @NotNull @DecimalMin("0.0") BigDecimal total_amount
) {}
