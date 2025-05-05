package com.engine.domain.model;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class OrderBookSnapshot {
    private final int maxPriceLevel = 20;
    private final String security;
    
    public final TreeMap<BigDecimal, TreeSet<Order>> bids;
    public final TreeMap<BigDecimal, TreeSet<Order>> asks;
    
    public OrderBookSnapshot(final String security, final TreeMap<BigDecimal, TreeSet<Order>> bids, 
        final TreeMap<BigDecimal, TreeSet<Order>> asks) {
        this.bids = deepCopySide(bids);
        this.asks = deepCopySide(asks);
        this.security = security;
    }

    public String getSecurity() {
        return security;
    }

    private TreeMap<BigDecimal, TreeSet<Order>> deepCopySide(final TreeMap<BigDecimal, TreeSet<Order>> side) {
        TreeMap<BigDecimal, TreeSet<Order>> deepCopy = new TreeMap<>();
        int priceLevel = 0;

        for (Map.Entry<BigDecimal, TreeSet<Order>> level : side.entrySet()) {
            TreeSet<Order> orders = level.getValue();
            if (priceLevel > maxPriceLevel) {
                break;
            }

            TreeSet<Order> orderCopies = new TreeSet<>();
            for (Order order : orders) {
                if (!order.isCancelled()) {
                    orderCopies.add(order.copy());
                }
            }

            if (!orderCopies.isEmpty()) {
                deepCopy.put(new BigDecimal(level.getKey().toString()), orderCopies);
                priceLevel++;
            }
        }

        return deepCopy;
    }
}