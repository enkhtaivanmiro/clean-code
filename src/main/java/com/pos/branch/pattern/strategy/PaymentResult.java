package com.pos.branch.pattern.strategy;

public record PaymentResult(boolean success, String message, String transactionId) {}
