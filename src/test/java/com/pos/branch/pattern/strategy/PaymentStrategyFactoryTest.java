package com.pos.branch.pattern.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class PaymentStrategyFactoryTest {

    private PaymentStrategyFactory factory;
    private PaymentStrategy cashStrategy;
    private PaymentStrategy cardStrategy;

    @BeforeEach
    void setUp() {
        cashStrategy = mock(CashPaymentStrategy.class);
        cardStrategy = mock(CardPaymentStrategy.class);
        Map<String, PaymentStrategy> strategies = new HashMap<>();
        strategies.put("CASH", cashStrategy);
        strategies.put("CARD", cardStrategy);
        factory = new PaymentStrategyFactory(strategies);
    }

    @Test
    void testGetStrategy_Success() {
        assertEquals(cashStrategy, factory.getStrategy("CASH"));
        assertEquals(cardStrategy, factory.getStrategy("card"));
    }

    @Test
    void testGetStrategy_Unknown() {
        assertThrows(IllegalArgumentException.class, () -> factory.getStrategy("BITCOIN"));
    }
}
