package com.pos.branch.pattern.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class POSSyncNotifier implements PriceChangeObserver {
    private static final Logger logger = LoggerFactory.getLogger(POSSyncNotifier.class);
    private final PriceChangePublisher publisher;

    public POSSyncNotifier(PriceChangePublisher publisher) {
        this.publisher = publisher;
    }

    @PostConstruct
    public void init() {
        publisher.subscribe(this);
    }

    @Override
    public void onPriceChange(PriceChangeEvent event) {
        logger.info("Price changed for product {}: {} -> {}. Sync with POS terminals needed.", 
                event.productId(), event.oldPrice(), event.newPrice());
    }
}
