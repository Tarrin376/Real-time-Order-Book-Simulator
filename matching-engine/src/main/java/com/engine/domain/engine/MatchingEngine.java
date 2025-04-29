package com.engine.domain.engine;

import com.engine.domain.model.Execution;
import com.engine.domain.model.Order;
import com.engine.domain.orderbook.OrderBook;
import com.engine.domain.orderbook.OrderBookManager;
import com.engine.enums.OrderSide;

public class MatchingEngine {
    private final ExecutionHandler executionHandler;
    private final OrderBookManager orderBookManager;

    public MatchingEngine(final ExecutionHandler executionHandler, final OrderBookManager orderBookManager) {
        this.executionHandler = executionHandler;
        this.orderBookManager = orderBookManager;
    }

    public void processNewOrder(final Order order) {
        System.out.println("Received order: " + order);
        OrderBook orderBook = orderBookManager.getOrCreateOrderBook(order.getTicker());

        switch (order.getType()) {
            case MARKET -> matchMarketOrder(order, orderBook);
        }
    }

    private void matchMarketOrder(final Order order, final OrderBook orderBook) {
        if (order.getSide() == OrderSide.BUY) {
            while (order.getQuantity() > 0 && orderBook.getLowestAsk() != null) {
                Order lowestAsk = orderBook.getLowestAsk();
                int buyQty = Math.min(order.getQuantity(), lowestAsk.getQuantity());
                lowestAsk.decreaseQuantity(buyQty);

                if (lowestAsk.isFilled()) {
                    orderBook.removeLowestAsk();
                }

                executionHandler.sendExecution(new Execution(OrderSide.SELL, order.getTicker(), lowestAsk.getPrice(), -buyQty));
                order.decreaseQuantity(buyQty);
            }

            if (!order.isFilled()) {
                executionHandler.sendExecution(new Execution(OrderSide.BUY, order.getTicker(), order.getPrice(), order.getQuantity()));
                orderBook.addBid(order);
            }
        } else {
            while (order.getQuantity() > 0 && orderBook.getHighestBid() != null) {
                Order highestBid = orderBook.getHighestBid();
                int sellQty = Math.min(order.getQuantity(), highestBid.getQuantity());
                highestBid.decreaseQuantity(sellQty);

                if (highestBid.isFilled()) {
                    orderBook.removeHighestBid();
                }

                executionHandler.sendExecution(new Execution(OrderSide.BUY, order.getTicker(), highestBid.getPrice(), -sellQty));
                order.decreaseQuantity(sellQty);
            }

            if (!order.isFilled()) {
                executionHandler.sendExecution(new Execution(OrderSide.SELL, order.getTicker(), order.getPrice(), order.getQuantity()));
                orderBook.addAsk(order);
            }
        }
    }
}