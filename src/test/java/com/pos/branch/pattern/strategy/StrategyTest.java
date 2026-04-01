package com.pos.branch.pattern.strategy;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class StrategyTest {

    @Test
    void testCashStrategy() {
        CashPaymentStrategy strategy = new CashPaymentStrategy();
        assertDoesNotThrow(() -> strategy.process(new PaymentRequest(BigDecimal.TEN, "Test")));
    }

    @Test
    void testCardStrategy() {
        CardPaymentStrategy strategy = new CardPaymentStrategy();
        assertDoesNotThrow(() -> strategy.process(new PaymentRequest(BigDecimal.TEN, "Test")));
    }

    @Test
    void testQRPaymentStrategy() {
        QRPaymentStrategy strategy = new QRPaymentStrategy();
        assertDoesNotThrow(() -> strategy.process(new PaymentRequest(BigDecimal.TEN, "Test")));
    }
}
