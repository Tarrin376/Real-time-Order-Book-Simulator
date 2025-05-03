package com.engine.domain.orderbook;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class OrderBookManager {
    public final ConcurrentMap<String, OrderBook> orderBooks;

    public OrderBookManager() {
        orderBooks = new ConcurrentHashMap<>();
    }

    public OrderBook getOrCreateOrderBook(final String security) {
        if (!orderBooks.containsKey(security)) {
            orderBooks.put(security, new OrderBook(security));
        }

        return orderBooks.get(security);
    }
}