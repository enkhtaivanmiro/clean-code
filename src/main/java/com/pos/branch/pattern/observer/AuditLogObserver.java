package com.pos.branch.pattern.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class AuditLogObserver implements PriceChangeObserver {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogObserver.class);
    private final PriceChangePublisher publisher;

    public AuditLogObserver(PriceChangePublisher publisher) {
        this.publisher = publisher;
    }

    @PostConstruct
    public void init() {
        publisher.subscribe(this);
    }

    @Override
    public void onPriceChange(PriceChangeEvent event) {
        logger.info("[AUDIT] Product {} price updated from {} to {} at {}", 
                event.productId(), event.oldPrice(), event.newPrice(), event.timestamp());
    }
}
