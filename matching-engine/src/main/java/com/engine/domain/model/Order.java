package com.engine.domain.model;

import java.math.BigDecimal;
import java.util.Date;

import com.engine.enums.OrderSide;
import com.engine.enums.OrderType;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Order implements Comparable<Order> {
    @JsonProperty("type")
    private OrderType type;

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
    private String orderId;

    @JsonProperty("timestamp")
    private double timestamp;

    private boolean cancelled;

    public OrderType getType() { return type; }
    public String getCancelOrderId() { return cancelOrderId; }
    public OrderSide getSide() { return side; }
    public String getSecurity() { return security; }
    public BigDecimal getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public String getOrderId() { return orderId; }
    public double getTimestamp() { return timestamp; }
    public boolean isCancelled() { return cancelled; }

    public void decreaseQuantity(final int amount) {
        if (quantity != null) {
            quantity -= Math.min(quantity, amount);
        } 
    }

    public Order copy() {
        Order order = new Order();

        order.type = this.type;
        order.cancelOrderId = this.cancelOrderId;
        order.side = this.side;
        order.security = this.security;
        order.price = this.price;
        order.quantity = this.quantity;
        order.orderId = this.orderId;
        order.timestamp = this.timestamp;
        order.cancelled = this.cancelled;

        return order;
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
            return "[" + orderId + "] " + type + " " + cancelOrderId + " " + timestampToString();
        }

        final String priceStr = price != null ? "£" + price + " | " : "";
        final String quantityStr = quantity != null ? "(x" + quantity + ") | " : "";
        return "[" + orderId + "] " + type + " " + side + " " + security + " | " + priceStr + quantityStr + timestampToString();
    }

    @Override
    public int compareTo(final Order other) {
        int cmp = Double.compare(this.getTimestamp(), other.getTimestamp());
        if (cmp != 0) {
            return cmp;
        }
    
        return this.getOrderId().compareTo(other.getOrderId());
    }
}
