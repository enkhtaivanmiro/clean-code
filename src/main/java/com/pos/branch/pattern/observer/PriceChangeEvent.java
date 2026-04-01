package com.pos.branch.pattern.observer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PriceChangeEvent(Integer productId, BigDecimal oldPrice, BigDecimal newPrice, LocalDateTime timestamp) {}
