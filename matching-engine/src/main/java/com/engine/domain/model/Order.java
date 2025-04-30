package com.engine.domain.model;

import java.util.Date;

import com.engine.enums.OrderSide;
import com.engine.enums.OrderType;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Order {
    @JsonProperty("type")
    private OrderType type;

    @JsonProperty("side")
    private OrderSide side;

    @JsonProperty("security")
    private String security;

    @JsonProperty("price")
    private double price;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("orderId")
    private String id;

    @JsonProperty("timestamp")
    private double timestamp;

    public OrderType getType() { return type; }
    public OrderSide getSide() { return side; }
    public String getSecurity() { return security; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getId() { return id; }
    public double getTimestamp() { return timestamp; }

    public void decreaseQuantity(final int amount) {
        quantity -= Math.min(quantity, amount);
    }

    public boolean isFilled() {
        return quantity == 0;
    }

    private String timestampToString() {
        Date date = new Date((long)(timestamp * 1000));
        return date.toString();
    }

    @Override
    public String toString() {
        return "[" + id + "] " + type + " " + side + " " + security + " | £" + price + " " + quantity + "x (" + timestampToString() + ")";
    }
}
