package com.engine.domain.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engine.domain.model.Execution;
import com.engine.domain.model.Order;
import com.engine.domain.orderbook.OrderBook;
import com.engine.domain.orderbook.OrderBookManager;
import com.engine.enums.OrderSide;

public class MatchingEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingEngine.class);
    private final ExecutionHandler executionHandler;
    private final OrderBookManager orderBookManager;

    public MatchingEngine(final ExecutionHandler executionHandler, final OrderBookManager orderBookManager) {
        this.executionHandler = executionHandler;
        this.orderBookManager = orderBookManager;
    }

    public void processNewOrder(final Order order) {
        LOGGER.info("Received order: " + order);
        OrderBook orderBook = orderBookManager.getOrCreateOrderBook(order.getSecurity());

        switch (order.getType()) {
            case LIMIT -> matchLimitOrMarketOrder(order, orderBook);
            case MARKET -> matchLimitOrMarketOrder(order, orderBook);
            case CANCEL -> orderBook.cancelOrder(order);
        }
    }

    private void matchLimitOrMarketOrder(final Order order, final OrderBook orderBook) {
        if (order.getSide() == OrderSide.BUY) {
            while (order.getQuantity() > 0 && orderBook.getLowestAsk() != null) {
                Order lowestAsk = orderBook.getLowestAsk();
                if (order.getPrice() != null && lowestAsk.getPrice() > order.getPrice()) {
                    break;
                }

                int buyQty = Math.min(order.getQuantity(), lowestAsk.getQuantity());
                lowestAsk.decreaseQuantity(buyQty);

                if (lowestAsk.isFilled()) {
                    orderBook.removeLowestAsk();
                }

                executionHandler.sendExecution(new Execution(lowestAsk.getId(), OrderSide.SELL, lowestAsk.getSecurity(), lowestAsk.getPrice(), buyQty));
                order.decreaseQuantity(buyQty);
            }

            if (!order.isFilled() && order.getPrice() != null) {
                executionHandler.sendExecution(new Execution(order.getId(), OrderSide.BUY, order.getSecurity(), order.getPrice(), order.getQuantity()));
                orderBook.addBid(order);
            }
        } else {
            while (order.getQuantity() > 0 && orderBook.getHighestBid() != null) {
                Order highestBid = orderBook.getHighestBid();
                if (order.getPrice() != null && highestBid.getPrice() < order.getPrice()) {
                    break;
                }

                int sellQty = Math.min(order.getQuantity(), highestBid.getQuantity());
                highestBid.decreaseQuantity(sellQty);

                if (highestBid.isFilled()) {
                    orderBook.removeHighestBid();
                }

                executionHandler.sendExecution(new Execution(highestBid.getId(), OrderSide.BUY, highestBid.getSecurity(), highestBid.getPrice(), sellQty));
                order.decreaseQuantity(sellQty);
            }

            if (!order.isFilled() && order.getPrice() != null) {
                executionHandler.sendExecution(new Execution(order.getId(), OrderSide.SELL, order.getSecurity(), order.getPrice(), order.getQuantity()));
                orderBook.addAsk(order);
            }
        }
    }
}