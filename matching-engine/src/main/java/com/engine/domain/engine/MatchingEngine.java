package com.engine.domain.engine;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engine.domain.model.Execution;
import com.engine.domain.model.Order;
import com.engine.domain.orderbook.OrderBook;
import com.engine.domain.orderbook.OrderBookManager;
import com.engine.enums.OrderSide;
import com.engine.enums.OrderType;
import com.engine.kafka.producers.ExecutionProducer;

public class MatchingEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingEngine.class);
    private final ExecutionProducer executionHandler;
    private final OrderBookManager orderBookManager;

    public MatchingEngine(final ExecutionProducer executionHandler, final OrderBookManager orderBookManager) {
        this.orderBookManager = orderBookManager;
        this.executionHandler = executionHandler;
    }

    public void processNewOrder(final Order order) {
        LOGGER.info("Received order: " + order);
        OrderBook orderBook = orderBookManager.getOrCreateOrderBook(order.getSecurity());

        orderBook.withLock(() -> {
            if (order.getType() == OrderType.CANCEL) orderBook.cancelOrder(order);
            else matchLimitOrMarketOrder(order, orderBook);
            return null;
        });
    }

    private boolean ordersMatch(final Order order, final BigDecimal price) {
        if (order.getSide() == OrderSide.BUY) {
            return order.getPrice().compareTo(price) >= 0;
        } else {
            return order.getPrice().compareTo(price) <= 0;
        }
    }

    private void matchLimitOrMarketOrder(final Order order, final OrderBook orderBook) {
        OrderBook.BIterator iterator = orderBook.new BIterator();

        while (!order.isFilled()) {
            Order nextOrder = order.getSide() == OrderSide.BUY ? iterator.nextAsk() : iterator.nextBid();
            if (nextOrder == null || (order.getType() == OrderType.LIMIT && !ordersMatch(order, nextOrder.getPrice()))) {
                break;
            }

            if (nextOrder.isCancelled()) {
                if (nextOrder.getSide() == OrderSide.BUY) iterator.removeBid();
                else iterator.removeAsk();
                continue;
            }

            int quantity = Math.min(order.getQuantity(), nextOrder.getQuantity());
            nextOrder.decreaseQuantity(quantity);
            order.decreaseQuantity(quantity);
            
            Execution newOrderExec = new Execution(nextOrder.getOrderId(), nextOrder.getSide(), nextOrder.getSecurity(), nextOrder.getPrice(), quantity);
            Execution orderExec = new Execution(order.getOrderId(), order.getSide(), order.getSecurity(), nextOrder.getPrice(), quantity);

            executionHandler.sendExecution(newOrderExec);
            executionHandler.sendExecution(orderExec);

            if (nextOrder.isFilled()) {
                orderBook.removePendingOrder(nextOrder);
                if (nextOrder.getSide() == OrderSide.BUY) iterator.removeBid();
                else iterator.removeAsk();
            }
        }

        if (!order.isFilled() && order.getType() == OrderType.LIMIT) {
            if (order.getSide() == OrderSide.BUY) orderBook.addBid(order);
            else orderBook.addAsk(order);
        }
    }
}