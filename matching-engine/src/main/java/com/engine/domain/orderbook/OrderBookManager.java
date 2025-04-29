package com.engine.domain.orderbook;

import java.util.HashMap;
import java.util.Map;

public class OrderBookManager {
    private final Map<String, OrderBook> orderBooks;

    public OrderBookManager() {
        orderBooks = new HashMap<>();
    }

    public OrderBook getOrCreateOrderBook(final String ticker) {
        if (!orderBooks.containsKey(ticker)) {
            orderBooks.put(ticker, new OrderBook());
        }

        return orderBooks.get(ticker);
    }
}