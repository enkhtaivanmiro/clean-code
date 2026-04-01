package com.pos.branch.pattern.strategy;

import org.springframework.stereotype.Component;
import java.util.UUID;

@Component("QR")
public class QRPaymentStrategy implements PaymentStrategy {
    @Override
    public PaymentResult process(PaymentRequest request) {
        // Simplified QR processing
        return new PaymentResult(true, "QR payment successful", UUID.randomUUID().toString());
    }
}
