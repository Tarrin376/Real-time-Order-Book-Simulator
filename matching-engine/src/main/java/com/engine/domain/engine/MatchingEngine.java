package com.engine.domain.engine;

import com.engine.domain.model.Order;
import com.engine.domain.model.OrderBook;

public class MatchingEngine {
    private final TradeHandler tradeHandler;
    private final OrderBook orderBook;

    public MatchingEngine(final TradeHandler tradeHandler, final OrderBook orderBook) {
        this.tradeHandler = tradeHandler;
        this.orderBook = orderBook;
    }

    public void processNewOrder(final Order order) {
        System.out.println(order);
        tradeHandler.publishTrade(null);
    }
}
