package com.engine;

import com.engine.domain.engine.MatchingEngine;
import com.engine.domain.engine.OrderHandler;
import com.engine.domain.engine.TradeHandler;
import com.engine.domain.model.OrderBook;

public class App {
    public static void main(final String[] args) {
        TradeHandler tradeHandler = new TradeHandler();
        OrderBook orderBook = new OrderBook();
        MatchingEngine matchingEngine = new MatchingEngine(tradeHandler, orderBook);

        OrderHandler orderHandler = new OrderHandler(matchingEngine);
        orderHandler.consumeOrders();
    }
}