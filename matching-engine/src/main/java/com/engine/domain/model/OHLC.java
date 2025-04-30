package com.engine.domain.model;

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

    @JsonProperty("ticker")
    private final String ticker;

    @JsonProperty("startTimestamp")
    private final double startTimestamp;

    @JsonProperty("endTimestamp")
    private final double endTimestamp;

    public OHLC(final double open, final double high, final double low, final double close, 
        final String ticker, final double startTimestamp, final double endTimestamp) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.ticker = ticker;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    public String getTicker() {
        return ticker;
    }
}