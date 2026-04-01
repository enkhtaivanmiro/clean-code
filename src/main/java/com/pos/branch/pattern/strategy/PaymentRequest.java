package com.pos.branch.pattern.strategy;

import java.math.BigDecimal;

public record PaymentRequest(BigDecimal amount, String details) {}
