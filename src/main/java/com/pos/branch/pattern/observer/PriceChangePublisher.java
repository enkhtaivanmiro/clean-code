package com.pos.branch.pattern.observer;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class PriceChangePublisher {
    private final List<PriceChangeObserver> observers = new ArrayList<>();

    public void subscribe(PriceChangeObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(PriceChangeObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(PriceChangeEvent event) {
        for (PriceChangeObserver observer : observers) {
            observer.onPriceChange(event);
        }
    }
}
