package com.engine.domain.model;

import java.util.UUID;

import com.engine.enums.OrderSide;

public class Execution {
    private final OrderSide side;
    private final String ticker;
    private final double price;
    private final int delta;
    private final UUID batchId;

    public Execution(final OrderSide side, final String ticker, final double price, final int delta, final UUID batchId) {
        this.side = side;
        this.ticker = ticker;
        this.price = price;
        this.delta = delta;
        this.batchId = batchId;
    }

    public String getTicker() {
        return ticker;
    }
}