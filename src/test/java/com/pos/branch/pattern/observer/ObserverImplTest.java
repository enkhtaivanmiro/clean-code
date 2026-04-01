package com.pos.branch.pattern.observer;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.mockito.Mockito.*;

public class ObserverImplTest {

    @Test
    void testPOSSyncNotifier() {
        PriceChangePublisher publisher = mock(PriceChangePublisher.class);
        POSSyncNotifier notifier = new POSSyncNotifier(publisher);
        notifier.init();
        verify(publisher).subscribe(notifier);

        PriceChangeEvent event = new PriceChangeEvent(1, BigDecimal.ZERO, BigDecimal.TEN, LocalDateTime.now());
        notifier.onPriceChange(event);
    }

    @Test
    void testAuditLogObserver() {
        PriceChangePublisher publisher = mock(PriceChangePublisher.class);
        AuditLogObserver observer = new AuditLogObserver(publisher);
        observer.init();
        verify(publisher).subscribe(observer);

        PriceChangeEvent event = new PriceChangeEvent(1, BigDecimal.ZERO, BigDecimal.TEN, LocalDateTime.now());
        observer.onPriceChange(event);
    }
}
