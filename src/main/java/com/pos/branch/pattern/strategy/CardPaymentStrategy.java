package com.pos.branch.pattern.strategy;

import org.springframework.stereotype.Component;
import java.util.UUID;

@Component("CARD")
public class CardPaymentStrategy implements PaymentStrategy {
    @Override
    public PaymentResult process(PaymentRequest request) {
        // Simplified card processing
        return new PaymentResult(true, "Card payment successful", UUID.randomUUID().toString());
    }
}
