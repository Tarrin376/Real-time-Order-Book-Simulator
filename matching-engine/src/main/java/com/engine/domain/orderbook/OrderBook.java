package com.engine.domain.orderbook;

import java.util.PriorityQueue;

import com.engine.domain.model.Order;

public class OrderBook {
    private PriorityQueue<Order> bids;
    private PriorityQueue<Order> asks;

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

    public Order getHighestBid() {
        return bids.isEmpty() ? null : bids.peek();
    }

    public void removeHighestBid() {
        if (!bids.isEmpty()) {
            bids.poll();
        }
    }

    public void addBid(final Order bid) {
        bids.offer(bid);
    }

    public Order getLowestAsk() {
        return asks.isEmpty() ? null : asks.peek();
    }

    public void removeLowestAsk() {
        if (!asks.isEmpty()) {
            asks.poll();
        }
    }

    public void addAsk(final Order ask) {
        asks.offer(ask);
    }
}