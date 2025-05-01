package com.engine.domain.model;

import java.util.Date;

import com.engine.enums.OrderSide;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Execution {
    @JsonProperty("orderId")
    private final String id;
    
    @JsonProperty("side")
    private final OrderSide side;

    @JsonProperty("security")
    private final String security;

    @JsonProperty("price")
    private final Double price;

    @JsonProperty("delta")
    private final int delta;

    @JsonProperty("timestamp")
    private final double timestamp;

    public Execution(final String id, final OrderSide side, final String security, final Double price, final int delta) {
        this.id = id;
        this.side = side;
        this.security = security;
        this.price = price;
        this.delta = delta;
        this.timestamp = (double)System.currentTimeMillis() / 1000;
    }

    public String getSecurity() {
        return security;
    }

    public Double getPrice() {
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