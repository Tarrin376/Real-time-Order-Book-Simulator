package com.engine.domain.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OHLC {
    @JsonProperty("open")
    private final double open;

    @JsonProperty("high")
    private final double high;

    @JsonProperty("low")
    private final double low;

    @JsonProperty("close")
    private final double close;

    @JsonProperty("security")
    private final String security;

    @JsonProperty("startTimestamp")
    private final double startTimestamp;

    @JsonProperty("endTimestamp")
    private final double endTimestamp;

    public OHLC(final double open, final double high, final double low, final double close, 
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
        return "[" + security + "]" + " Open: " + open + " | High: " + high + " | Low: " + low + " | Close: " + close + 
        " (From: " + toDateString(startTimestamp) + ", To: " + toDateString(endTimestamp) + ")";
    }
}