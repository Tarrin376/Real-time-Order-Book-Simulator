package com.engine.domain.orderbook;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import com.engine.domain.model.Order;
import com.engine.domain.model.OrderBookSnapshot;

public class OrderBook {
    private final TreeMap<BigDecimal, TreeSet<Order>> bids;
    private final TreeMap<BigDecimal, TreeSet<Order>> asks;
    private final Map<String, Order> pendingOrders;

    private final ReentrantLock lock = new ReentrantLock();
    private final String security;

    protected OrderBook(final String security) {
        this.security = security;
        bids = new TreeMap<>(Collections.reverseOrder());
        asks = new TreeMap<>();
        pendingOrders = new HashMap<>();
    }

    public String getSecurity() {
        return security;
    }

    public <T> T withLock(Supplier<T> operation) {
        try {
            lock.lock();
            return operation.get();
        } finally {
            lock.unlock();
        }
    }

    public void addBid(final Order bid) {
        if (!bids.containsKey(bid.getPrice())) {
            bids.put(bid.getPrice(), new TreeSet<>());
        }

        TreeSet orders = bids.get(bid.getPrice());
        pendingOrders.put(bid.getOrderId(), bid);
        orders.add(bid);
    }

    public void addAsk(final Order ask) {
        if (!asks.containsKey(ask.getPrice())) {
            asks.put(ask.getPrice(), new TreeSet<>());
        }

        TreeSet orders = asks.get(ask.getPrice());
        pendingOrders.put(ask.getOrderId(), ask);
        orders.add(ask);
    }

    public Order cancelOrder(final Order order) {
        if (pendingOrders.containsKey(order.getCancelOrderId())) {
            Order cancelledOrder = pendingOrders.get(order.getCancelOrderId());
            pendingOrders.remove(order.getCancelOrderId());
            cancelledOrder.cancelOrder();
            return cancelledOrder;
        }

        return null;
    }

    public void removePendingOrder(final Order order) {
        if (pendingOrders.containsKey(order.getOrderId())) {
            pendingOrders.remove(order.getOrderId());
        }
    }

    public OrderBookSnapshot getSnapshot() {
        OrderBookSnapshot orderBookSnapshot = new OrderBookSnapshot(security, bids, asks);
        return orderBookSnapshot;
    }

    public class OrderIterator {
        private BigDecimal bidLevel;
        private BigDecimal askLevel;

        private Iterator<Order> curBids;
        private Iterator<Order> curAsks;

        public OrderIterator() {
            bidLevel = bids.isEmpty() ? null : bids.firstKey();
            askLevel = asks.isEmpty() ? null : asks.firstKey();

            curBids = bidLevel != null ? bids.get(bidLevel).iterator() : Collections.emptyIterator();
            curAsks = askLevel != null ? asks.get(askLevel).iterator() : Collections.emptyIterator();
        }

        public Order nextAsk() {
            while (!curAsks.hasNext() && askLevel != null) {
                BigDecimal nextAskLevel = asks.higherKey(askLevel);
                if (nextAskLevel == null) {
                    break;
                }

                curAsks = asks.get(nextAskLevel).iterator();
                askLevel = nextAskLevel;
            }
            
            return curAsks.hasNext() ? curAsks.next() : null;
        }

        public Order nextBid() {
            while (!curBids.hasNext() && bidLevel != null) {
                BigDecimal nextBidLevel = bids.higherKey(bidLevel);
                if (nextBidLevel == null) {
                    break;
                }

                curBids = bids.get(nextBidLevel).iterator();
                bidLevel = nextBidLevel;
            }
            
            return curBids.hasNext() ? curBids.next() : null;
        }

        public void removeAsk() {
            curAsks.remove();
        }

        public void removeBid() {
            curBids.remove();
        }
    }
}