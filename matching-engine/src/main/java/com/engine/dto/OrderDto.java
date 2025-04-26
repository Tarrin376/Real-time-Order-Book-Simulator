package com.engine.dto;

import com.engine.enums.OrderSide;
import com.engine.enums.OrderType;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderDto {
    @JsonProperty("type")
    private OrderType type;

    @JsonProperty("side")
    private OrderSide side;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("price")
    private double price;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("order_id")
    private String id;

    public OrderType getType() { return type; }
    public OrderSide getSide() { return side; }
    public String getSymbol() { return symbol; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getId() { return id; }

    @Override
    public String toString() {
        return "Type: " + type + " | Side: " + side + " | Symbol: " + symbol + " | Price: " + price + " | Quantity: " + quantity + " | Order ID: " + id;
    }
}
