package com.engine;

import com.engine.domain.engine.MatchingEngine;
import com.engine.domain.orderbook.OrderBookManager;
import com.engine.kafka.consumers.OrderConsumer;
import com.engine.kafka.producers.ExecutionProducer;
import com.engine.kafka.producers.OHLCProducer;
import com.engine.kafka.producers.OrderBookSnapshotProducer;

public class App {
    public static void main(final String[] args) {
        OrderBookManager orderBookManager = new OrderBookManager();
        ExecutionProducer executionProducer = new ExecutionProducer(new OHLCProducer());
        MatchingEngine matchingEngine = new MatchingEngine(executionProducer, orderBookManager);

        OrderBookSnapshotProducer snapshotProducer = new OrderBookSnapshotProducer(orderBookManager);
        snapshotProducer.run();

        OrderConsumer orderConsumer = new OrderConsumer(matchingEngine);
        orderConsumer.consume();
    }
}