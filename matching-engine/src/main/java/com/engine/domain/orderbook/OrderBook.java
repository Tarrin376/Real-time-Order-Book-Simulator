package com.engine.domain.orderbook;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import com.engine.domain.model.Order;
import com.engine.domain.model.OrderBookSnapshot;

public class OrderBook {
    private final TreeMap<BigDecimal, TreeSet<Order>> bids;
    private final TreeMap<BigDecimal, TreeSet<Order>> asks;

    // Thread confinement: only used by the matching engine thread
    private final Map<String, Order> pendingOrders;

    private final String security;
    private final AtomicInteger seqId;
    public final ReentrantLock lock = new ReentrantLock();

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
        if (!bids.containsKey(bid.getPrice())) {
            bids.put(bid.getPrice(), new TreeSet<>((a, b) -> a.compareTo(b)));
        }

        TreeSet orders = bids.get(bid.getPrice());
        pendingOrders.put(bid.getId(), bid);
        orders.add(bid);
    }

    public void addAsk(final Order ask) {
        if (!asks.containsKey(ask.getPrice())) {
            asks.put(ask.getPrice(), new TreeSet<>((a, b) -> a.compareTo(b)));
        }

        TreeSet orders = asks.get(ask.getPrice());
        pendingOrders.put(ask.getId(), ask);
        orders.add(ask);
    }

    public void cancelOrder(final Order order) {
        try {
            lock.lock();
            if (!pendingOrders.containsKey(order.getCancelOrderId())) {
                return;
            }

            Order cancelledOrder = pendingOrders.get(order.getCancelOrderId());
            pendingOrders.remove(order.getCancelOrderId());
            cancelledOrder.cancelOrder();
        } finally{
            lock.unlock();
        }
    }

    public void removePendingOrder(final Order order) {
        if (pendingOrders.containsKey(order.getId())) {
            pendingOrders.remove(order.getId());
        }
    }

    public OrderBookSnapshot getSnapshot() {
        try {
            lock.lock();
            OrderBookSnapshot orderBookSnapshot = new OrderBookSnapshot(security, bids, asks, seqId.intValue());
            seqId.addAndGet(1);
            return orderBookSnapshot;
        } finally {
            lock.unlock();
        }
    }

    public class BIterator {
        private BigDecimal bidLevel;
        private BigDecimal askLevel;

        private Iterator<Order> curBids;
        private Iterator<Order> curAsks;

        public BIterator() {
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