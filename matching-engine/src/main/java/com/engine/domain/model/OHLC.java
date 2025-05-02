package com.engine.domain.model;

import java.util.Date;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OHLC {
    @JsonProperty("open")
    private final BigDecimal open;

    @JsonProperty("high")
    private final BigDecimal high;

    @JsonProperty("low")
    private final BigDecimal low;

    @JsonProperty("close")
    private final BigDecimal close;

    @JsonProperty("security")
    private final String security;

    @JsonProperty("startTimestamp")
    private final double startTimestamp;

    @JsonProperty("endTimestamp")
    private final double endTimestamp;

    public OHLC(final BigDecimal open, final BigDecimal high, final BigDecimal low, final BigDecimal close, 
        final String security, final double startTimestamp, final double endTimestamp) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.security = security;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    public String getSecurity() {
        return security;
    }

    private String toDateString(final double timestamp) {
        Date date = new Date((long)(timestamp * 1000));
        return date.toString();
    }

    @Override
    public String toString() {
        return "[" + security + "]" + " Open: £" + open + " | High: £" + high + " | Low: £" + low + " | Close: £" + close + 
        " (" + toDateString(startTimestamp) + " - " + toDateString(endTimestamp) + ")";
    }
}