package com.pos.branch.event;

import com.pos.branch.model.Sale;
import org.springframework.context.ApplicationEvent;

public class SaleCreatedEvent extends ApplicationEvent {
    private final Sale sale;

    public SaleCreatedEvent(Object source, Sale sale) {
        super(source);
        this.sale = sale;
    }

    public Sale getSale() {
        return sale;
    }
}
