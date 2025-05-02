package com.engine;

import com.engine.domain.engine.ExecutionHandler;
import com.engine.domain.engine.MatchingEngine;
import com.engine.domain.engine.OrderHandler;
import com.engine.domain.engine.OrderBookManager;

public class App {
    public static void main(final String[] args) {
        MatchingEngine matchingEngine = new MatchingEngine(new ExecutionHandler(), new OrderBookManager());
        OrderHandler orderHandler = new OrderHandler(matchingEngine);
        orderHandler.consumeOrders();
    }
}