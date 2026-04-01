package com.pos.branch.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record SaleItemRequest(
    @NotNull @Positive Integer product_id,
    @NotNull @Positive Integer quantity,
    @NotNull @DecimalMin("0.0") BigDecimal price,
    Integer discount_rule_id,
    @NotNull @DecimalMin("0.0") BigDecimal discount_amount
) {}
