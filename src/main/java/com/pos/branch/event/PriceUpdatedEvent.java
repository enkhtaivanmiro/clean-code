package com.pos.branch.event;

import org.springframework.context.ApplicationEvent;
import java.math.BigDecimal;

public class PriceUpdatedEvent extends ApplicationEvent {
    private final Integer productId;
    private final BigDecimal newPrice;

    public PriceUpdatedEvent(Object source, Integer productId, BigDecimal newPrice) {
        super(source);
        this.productId = productId;
        this.newPrice = newPrice;
    }

    public Integer getProductId() {
        return productId;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }
}
