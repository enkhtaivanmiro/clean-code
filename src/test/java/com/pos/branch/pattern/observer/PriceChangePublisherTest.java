package com.pos.branch.pattern.observer;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.mockito.Mockito.*;

public class PriceChangePublisherTest {

    @Test
    void testNotifyObservers() {
        PriceChangePublisher publisher = new PriceChangePublisher();
        PriceChangeObserver observer = mock(PriceChangeObserver.class);
        publisher.subscribe(observer);

        PriceChangeEvent event = new PriceChangeEvent(1, new BigDecimal("10.0"), new BigDecimal("12.0"), LocalDateTime.now());
        publisher.notifyObservers(event);

        verify(observer, times(1)).onPriceChange(event);
    }
}
