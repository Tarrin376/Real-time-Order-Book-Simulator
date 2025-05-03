package com.engine.domain.model;

import java.math.BigDecimal;
import java.util.TreeMap;
import java.util.TreeSet;

public class OrderBookSnapshot {
    private final String security;
    private final int seqId;

    public final TreeMap<BigDecimal, TreeSet<Order>> bids;
    public final TreeMap<BigDecimal, TreeSet<Order>> asks;
    
    public OrderBookSnapshot(final String security, final TreeMap<BigDecimal, TreeSet<Order>> bids, 
        final TreeMap<BigDecimal, TreeSet<Order>> asks, final int seqId) {
        this.security = security;
        this.bids = bids;
        this.asks = asks;
        this.seqId = seqId;
    }

    public String getSecurity() {
        return security;
    }

    public int getSeqId() {
        return seqId;
    }
}