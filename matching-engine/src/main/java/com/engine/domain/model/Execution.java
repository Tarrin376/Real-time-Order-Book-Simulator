package com.engine.domain.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import com.engine.enums.OrderSide;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Execution {
    @JsonProperty("orderId")
    private final String id;

    @JsonProperty("id")
    private final String executionId;
    
    @JsonProperty("side")
    private final OrderSide side;

    @JsonProperty("security")
    private final String security;

    @JsonProperty("price")
    private final BigDecimal price;

    @JsonProperty("delta")
    private final int delta;

    @JsonProperty("timestamp")
    private final double timestamp;

    @JsonProperty("seqId")
    private final int seqId;

    public Execution(final String id, final OrderSide side, final String security, final BigDecimal price, final int delta, final int seqId) {
        this.id = id;
        this.side = side;
        this.security = security;
        this.price = price;
        this.delta = delta;
        this.seqId = seqId;
        this.timestamp = (double)System.currentTimeMillis() / 1000;
        this.executionId = UUID.randomUUID().toString();
    }

    public String getSecurity() {
        return security;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public double getTimestamp() {
        return timestamp;
    }

    private String timestampToString() {
        Date date = new Date((long)(timestamp * 1000));
        return date.toString();
    }

    @Override
    public String toString() {
        return "[" + id + "] " + side + " " + security + " | £" + price + " (x" + delta + ") | " + timestampToString();
    }
}