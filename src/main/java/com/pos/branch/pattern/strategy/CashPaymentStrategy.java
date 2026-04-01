package com.pos.branch.pattern.strategy;

import org.springframework.stereotype.Component;
import java.util.UUID;

@Component("CASH")
public class CashPaymentStrategy implements PaymentStrategy {
    @Override
    public PaymentResult process(PaymentRequest request) {
        // Simplified cash processing
        return new PaymentResult(true, "Cash payment successful", UUID.randomUUID().toString());
    }
}
