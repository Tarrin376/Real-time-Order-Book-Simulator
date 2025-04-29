package com.engine.domain.model;

import com.engine.enums.OrderSide;
import com.engine.enums.OrderType;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Order {
    @JsonProperty("type")
    private OrderType type;

    @JsonProperty("side")
    private OrderSide side;

    @JsonProperty("ticker")
    private String ticker;

    @JsonProperty("price")
    private double price;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("order_id")
    private String id;

    @JsonProperty("timestamp")
    private float timestamp;

    public OrderType getType() { return type; }
    public OrderSide getSide() { return side; }
    public String getTicker() { return ticker; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getId() { return id; }
    public float getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + id + "] | " + type + " | " + side + " | " + ticker + " | £" + price + " " + quantity + "x" + " (" + timestamp + ")";
    }
}
