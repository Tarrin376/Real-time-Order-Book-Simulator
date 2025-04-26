package com.engine.enums;

public enum OrderSide {
    BUY("BUY"),
    SELL("SELL");

    private final String orderSide;

    OrderSide(final String orderSide) {
        this.orderSide = orderSide;
    }

    @Override
    public String toString() {
        return orderSide;
    }
}
