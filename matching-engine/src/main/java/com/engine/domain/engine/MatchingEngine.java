package com.engine.domain.engine;

import com.engine.domain.model.Execution;
import com.engine.domain.model.Order;
import com.engine.domain.model.OrderBook;
import com.engine.enums.OrderSide;

public class MatchingEngine {
    private final ExecutionHandler executionHandler;
    private final OrderBook orderBook;

    public MatchingEngine(final ExecutionHandler executionHandler, final OrderBook orderBook) {
        this.executionHandler = executionHandler;
        this.orderBook = orderBook;
    }

    public void processNewOrder(final Order order) {
        System.out.println("Received order: " + order);
        executionHandler.sendExecution(new Execution(OrderSide.BUY, "TSLA", 100, 50));
    }
}
