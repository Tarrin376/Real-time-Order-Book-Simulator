package com.engine.domain.model;

import java.util.PriorityQueue;

public class OrderBook {
    protected PriorityQueue<Order> bids;
    protected PriorityQueue<Order> asks;

    public OrderBook() {
        bids = new PriorityQueue<>((a, b) -> {
            if (a.getPrice() != b.getPrice()) return Double.compare(b.getPrice(), a.getPrice());
            return Float.compare(b.getTimestamp(), a.getTimestamp());
        });

        asks = new PriorityQueue<>((a, b) -> {
            if (a.getPrice() != b.getPrice())  return Double.compare(a.getPrice(), b.getPrice());
            return Float.compare(b.getTimestamp(), a.getTimestamp());
        });
    }

    public void match(Order order) {

    }
}