package com.engine.domain.model;

import java.math.BigDecimal;
import java.util.Date;

import com.engine.enums.OrderSide;
import com.engine.enums.OrderType;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Order {
    @JsonProperty("type")
    private OrderType type;

    @JsonProperty("fok")
    private Boolean fillOrKill;

    @JsonProperty("cancelOrderId")
    private String cancelOrderId;

    @JsonProperty("side")
    private OrderSide side;

    @JsonProperty("security")
    private String security;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("orderId")
    private String id;

    @JsonProperty("timestamp")
    private double timestamp;

    private boolean cancelled;

    public OrderType getType() { return type; }
    public Boolean getFillOrKill() { return fillOrKill; }
    public String getCancelOrderId() { return cancelOrderId; }
    public OrderSide getSide() { return side; }
    public String getSecurity() { return security; }
    public BigDecimal getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public String getId() { return id; }
    public double getTimestamp() { return timestamp; }
    public boolean isCancelled() { return cancelled; }

    public void decreaseQuantity(final int amount) {
        if (quantity != null) {
            quantity -= Math.min(quantity, amount);
        }
    }

    public void cancelOrder() {
        cancelled = true;
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
        if (type == OrderType.CANCEL) {
            return "[" + id + "] " + type + " " + cancelOrderId + " " + timestampToString();
        }

        final String priceStr = price != null ? "£" + price + " | " : "";
        final String quantityStr = quantity != null ? "(x" + quantity + ") | " : "";
        final String fillOrKillStr = fillOrKill ? "FOK " : "";
        return "[" + id + "] " + fillOrKillStr + type + " " + side + " " + security + " | " + priceStr + quantityStr + timestampToString();
    }
}
