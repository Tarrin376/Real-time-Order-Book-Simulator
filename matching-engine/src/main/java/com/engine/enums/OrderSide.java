package com.engine.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderSide {
    BUY("BUY"),
    SELL("SELL");

    private final String orderSide;

    OrderSide(final String orderSide) {
        this.orderSide = orderSide;
    }

    @JsonValue
    public String getOrderSide() {
        return orderSide;
    }
}
