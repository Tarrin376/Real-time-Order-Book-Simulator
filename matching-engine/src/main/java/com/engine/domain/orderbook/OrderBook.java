package com.engine.domain.orderbook;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.engine.domain.model.Order;
import com.engine.domain.model.OrderBookSnapshot;

public class OrderBook {
    public final TreeMap<BigDecimal, TreeSet<Order>> bids;
    public final TreeMap<BigDecimal, TreeSet<Order>> asks;

    private final Map<String, Order> pendingOrders;
    private final String security;
    private final AtomicInteger seqId;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    public OrderBook(final String security) {
        this.security = security;
        bids = new TreeMap<>(Collections.reverseOrder());
        asks = new TreeMap<>();
        pendingOrders = new HashMap<>();
        seqId = new AtomicInteger();
    }

    public int getSeqId() {
        return seqId.intValue();
    }

    public void addBid(final Order bid) {
        try {
            writeLock.lock();
            if (!bids.containsKey(bid.getPrice())) {
                bids.put(bid.getPrice(), new TreeSet<>((a, b) -> Double.compare(a.getTimestamp(), b.getTimestamp())));
            }

            TreeSet orders = bids.get(bid.getPrice());
            pendingOrders.put(bid.getId(), bid);
            orders.add(bid);
        } finally {
            writeLock.unlock();
        }
    }

    public void addAsk(final Order ask) {
        try {
            writeLock.lock();
            if (!asks.containsKey(ask.getPrice())) {
                asks.put(ask.getPrice(), new TreeSet<>((a, b) -> Double.compare(a.getTimestamp(), b.getTimestamp())));
            }

            TreeSet orders = asks.get(ask.getPrice());
            pendingOrders.put(ask.getId(), ask);
            orders.add(ask);
        } finally {
            writeLock.unlock();
        }
    }

    public void cancelOrder(final Order order) {
        if (!pendingOrders.containsKey(order.getCancelOrderId())) {
            return;
        }

        Order cancelledOrder = pendingOrders.get(order.getCancelOrderId());
        pendingOrders.remove(order.getCancelOrderId());
        cancelledOrder.cancelOrder();
    }

    public void removePendingOrder(final Order order) {
        if (pendingOrders.containsKey(order.getId())) {
            pendingOrders.remove(order.getId());
        }
    }

    public OrderBookSnapshot getSnapshot() {
        try {
            readLock.lock();
            OrderBookSnapshot orderBookSnapshot = new OrderBookSnapshot(security, bids, asks, seqId.intValue());
            return orderBookSnapshot;
        } finally {
            seqId.addAndGet(1);
            readLock.unlock();
        }
    }
}