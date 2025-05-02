package com.engine.domain.engine;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Queue;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engine.domain.model.Execution;
import com.engine.domain.model.Order;
import com.engine.enums.OrderSide;
import com.engine.enums.OrderType;

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

        if (order.getType() == OrderType.LIMIT || order.getType() == OrderType.MARKET) {
            matchLimitOrMarketOrder(order, orderBook);
        } else {
            orderBook.cancelOrder(order);
        }
    }

    private boolean ordersMatch(final Order order, final BigDecimal price) {
        if (order.getSide() == OrderSide.BUY) {
            return order.getPrice().compareTo(price) <= 0;
        } else {
            return order.getPrice().compareTo(price) >= 0;
        }
    }

    private void matchLimitOrMarketOrder(final Order order, final OrderBook orderBook) {
        TreeMap<BigDecimal, TreeSet<Order>> orderMap = order.getSide() == OrderSide.BUY ? orderBook.asks : orderBook.bids;
        OrderSide oppositeSide = order.getSide() == OrderSide.BUY ? OrderSide.SELL : OrderSide.BUY;

        Queue<Order> matchedOrders = new LinkedList<>();
        int orderQuantity = order.getQuantity();

        for (Map.Entry<BigDecimal, TreeSet<Order>> entry : orderMap.entrySet()) {
            if (order.getType() == OrderType.LIMIT && !ordersMatch(order, entry.getKey())) {
                break;
            }

            Iterator<Order> orders = entry.getValue().iterator();
            while (orders.hasNext() && orderQuantity > 0) {
                Order matchedOrder = orders.next();
                int quantity = Math.min(orderQuantity, matchedOrder.getQuantity());

                if (!order.getFillOrKill()) {
                    executionHandler.sendExecution(new Execution(matchedOrder.getId(), oppositeSide, matchedOrder.getSecurity(), matchedOrder.getPrice(), quantity));
                    matchedOrder.decreaseQuantity(quantity);

                    if (matchedOrder.isFilled()) {
                        orders.remove();
                    }
                } else {
                    matchedOrders.offer(order);
                }

                orderQuantity -= quantity;
            }
        }

        if (order.getFillOrKill()) {
            fillOrKillOrder(order, orderBook, oppositeSide);
        } else if (!order.isFilled() && order.getPrice() != null) {
            order.decreaseQuantity(order.getQuantity() - orderQuantity);
            executionHandler.sendExecution(new Execution(order.getId(), order.getSide(), order.getSecurity(), order.getPrice(), order.getQuantity()));

            if (order.getSide() == OrderSide.BUY) {
                orderBook.addBid(order);
            } else {
                orderBook.addAsk(order);
            }
        }
    }

    private void fillOrKillOrder(final Order order, final OrderBook orderBook, final OrderSide oppositeSide) {
        if (!order.isFilled()) {
            LOGGER.info("Order rejected: " + order + " (Insufficient liquidity to fulfill entire quantity)");
            return;
        }

        for (Map.Entry<BigDecimal, TreeSet<Order>> entry : orderBook.asks.entrySet()) {
            Iterator<Order> orders = entry.getValue().iterator();
            boolean orderFilled = false;

            while (orders.hasNext()) {
                Order matchedOrder = orders.next();
                int quantity = Math.min(order.getQuantity(), matchedOrder.getQuantity());
                executionHandler.sendExecution(new Execution(matchedOrder.getId(), oppositeSide, matchedOrder.getSecurity(), matchedOrder.getPrice(), quantity));

                order.decreaseQuantity(quantity);
                matchedOrder.decreaseQuantity(quantity);

                if (matchedOrder.isFilled()) {
                    orders.remove();
                }

                if (order.isFilled()) {
                    orderFilled = true;
                    break;
                }
            }

            if (orderFilled) {
                break;
            }
        }
    }
}