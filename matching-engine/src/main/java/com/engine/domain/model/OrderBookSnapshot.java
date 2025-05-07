package com.engine.domain.model;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class OrderBookSnapshot {
    private final int maxPriceLevel = 20;
    private final String security;

    private final BigDecimal spread;
    private final BigDecimal bestBid;
    private final BigDecimal bestAsk;
    private final String liquidityRatio;
    private final Integer totalVolume;
    
    public final TreeMap<BigDecimal, TreeSet<Order>> bids;
    public final TreeMap<BigDecimal, TreeSet<Order>> asks;
    
    public OrderBookSnapshot(final String security, final TreeMap<BigDecimal, TreeSet<Order>> bids, 
        final TreeMap<BigDecimal, TreeSet<Order>> asks) {
        this.bids = deepCopySide(bids);
        this.asks = deepCopySide(asks);
        this.spread = computeSpread();
        this.bestBid = computeBestBid();
        this.bestAsk = computeBestAsk();
        this.liquidityRatio = computeLiquidityRatio();
        this.totalVolume = computeTotalVolume();
        this.security = security;
    }

    public String getSecurity() {
        return security;
    }

    public BigDecimal getSpread() {
        return spread;
    }

    public BigDecimal getBestBid() {
        return bestBid;
    }

    public BigDecimal getBestAsk() {
        return bestAsk;
    }

    public String getLiquidityRatio() {
        return liquidityRatio;
    }

    public int getTotalVolume() {
        return totalVolume;
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

    private BigDecimal computeSpread() {
        if (bids.isEmpty() || asks.isEmpty()) {
            return null;
        }
        
        return asks.firstKey().subtract(bids.lastKey());
    }

    private BigDecimal computeBestBid() {
        return bids.isEmpty() ? null : bids.lastKey();
    }

    private BigDecimal computeBestAsk() {
        return asks.isEmpty() ? null : asks.firstKey();
    }

    private String computeLiquidityRatio() {
        if (bids.isEmpty() || asks.isEmpty()) {
            return null;
        }

        int totalBidVolume = computeSideTotalVolume(bids);
        int totalAskVolume = computeSideTotalVolume(asks);
        return totalBidVolume + " / " + totalAskVolume;
    }

    private int computeSideTotalVolume(final TreeMap<BigDecimal, TreeSet<Order>> side) {
        int volume = 0;
        for (TreeSet<Order> level : side.values()) {
            for (Order order : level) {
                if (!order.isCancelled()) {
                    volume += order.getQuantity();
                }
            }
        }

        return volume;
    }

    private Integer computeTotalVolume() {
        int total = computeSideTotalVolume(bids) + computeSideTotalVolume(asks);
        return total == 0 ? null : total;
    }
}