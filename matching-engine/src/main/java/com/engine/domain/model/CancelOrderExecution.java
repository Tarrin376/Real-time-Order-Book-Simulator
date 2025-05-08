package com.engine.domain.model;

import java.math.BigDecimal;

import com.engine.enums.OrderSide;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CancelOrderExecution extends OrderExecution {
    @JsonProperty("cancelOrderId")
    private final String cancelOrderId;
    
    public CancelOrderExecution(final String orderId, final OrderSide side, final String security, 
        final BigDecimal price, final int delta, final String cancelOrderid) {
        super(orderId, side, security, price, delta);
        this.cancelOrderId = cancelOrderid;
    }
}