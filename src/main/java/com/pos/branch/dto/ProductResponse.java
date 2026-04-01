package com.pos.branch.dto;

import java.math.BigDecimal;

public record ProductResponse(Integer id, String name, String barcode, BigDecimal price, String category) {}
