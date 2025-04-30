package com.engine.domain.model;

import com.engine.enums.OrderSide;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Execution {
    @JsonProperty("side")
    private final OrderSide side;

    @JsonProperty("ticker")
    private final String ticker;

    @JsonProperty("price")
    private final double price;

    @JsonProperty("delta")
    private final int delta;

    @JsonProperty("timestamp")
    private final double timestamp;

    public Execution(final OrderSide side, final String ticker, final double price, final int delta) {
        this.side = side;
        this.ticker = ticker;
        this.price = price;
        this.delta = delta;
        this.timestamp = (double)System.currentTimeMillis() / 1000;
    }

    public String getTicker() {
        return ticker;
    }

    public double getPrice() {
        return price;
    }

    public double getTimestamp() {
        return timestamp;
    }
}