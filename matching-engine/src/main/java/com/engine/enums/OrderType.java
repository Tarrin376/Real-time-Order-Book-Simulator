package com.engine.enums;

public enum OrderType {
    MARKET("MARKET"),
    LIMIT("LIMIT"),
    CANCEL("CANCEL");

    private final String orderType;

    OrderType(final String orderType) {
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return orderType;
    }
}
