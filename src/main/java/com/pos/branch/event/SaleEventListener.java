package com.pos.branch.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SaleEventListener {
    private static final Logger log = LoggerFactory.getLogger(SaleEventListener.class);

    @EventListener
    public void handleSaleCreated(SaleCreatedEvent event) {
        log.info("Sale processed successfully: ID={}, Total={}", 
            event.getSale().getId(), event.getSale().getTotalAmount());
        // Additional side effects: trigger central sync if needed, audits, etc.
    }
}
