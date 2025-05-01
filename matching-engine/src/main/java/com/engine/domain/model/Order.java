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
    private Double price;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("orderId")
    private String id;

    @JsonProperty("timestamp")
    private double timestamp;

    public OrderType getType() { return type; }
    public OrderSide getSide() { return side; }
    public String getSecurity() { return security; }
    public Double getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public String getId() { return id; }
    public double getTimestamp() { return timestamp; }

    public void decreaseQuantity(final int amount) {
        if (quantity != null) {
            quantity -= Math.min(quantity, amount);
        }
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
        final String priceStr = price != null ? "£" + price + " | " : "";
        final String quantityStr = quantity != null ? "(x" + quantity + ") | " : "";
        return "[" + id + "] " + type + " " + side + " " + security + " | " + priceStr + quantityStr + timestampToString();
    }
}
