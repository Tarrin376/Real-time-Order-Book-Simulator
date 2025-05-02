package com.engine.domain.engine;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import com.engine.domain.model.Order;

public class OrderBook {
    protected final TreeMap<BigDecimal, TreeSet<Order>> bids;
    protected final TreeMap<BigDecimal, TreeSet<Order>> asks;
    private final Map<String, Order> placedOrders;

    public OrderBook() {
        bids = new TreeMap<>(Collections.reverseOrder());
        asks = new TreeMap<>();
        placedOrders = new HashMap<>();
    }

    public void addBid(final Order bid) {
        if (!bids.containsKey(bid.getPrice())) {
            bids.put(bid.getPrice(), new TreeSet<>((a, b) -> Double.compare(a.getTimestamp(), b.getTimestamp())));
        }

        TreeSet orders = bids.get(bid.getPrice());
        placedOrders.put(bid.getId(), bid);
        orders.add(bid);
    }

    public void addAsk(final Order ask) {
        if (!asks.containsKey(ask.getPrice())) {
            asks.put(ask.getPrice(), new TreeSet<>((a, b) -> Double.compare(a.getTimestamp(), b.getTimestamp())));
        }

        TreeSet orders = asks.get(ask.getPrice());
        placedOrders.put(ask.getId(), ask);
        orders.add(ask);
    }

    public void cancelOrder(final Order order) {
        if (!placedOrders.containsKey(order.getId())) {
            return;
        }
    }
}