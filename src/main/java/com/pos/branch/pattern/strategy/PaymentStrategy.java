package com.pos.branch.pattern.strategy;

public interface PaymentStrategy {
    PaymentResult process(PaymentRequest request);
}
