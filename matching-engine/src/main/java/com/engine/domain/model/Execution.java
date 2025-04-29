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

    public Execution(final OrderSide side, final String ticker, final double price, final int delta) {
        this.side = side;
        this.ticker = ticker;
        this.price = price;
        this.delta = delta;
    }

    public String getTicker() {
        return ticker;
    }
}