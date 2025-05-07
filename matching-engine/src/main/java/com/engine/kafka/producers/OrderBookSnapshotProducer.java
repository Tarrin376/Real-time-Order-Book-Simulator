package com.engine.kafka.producers;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engine.domain.model.OrderBookSnapshot;
import com.engine.domain.orderbook.OrderBook;
import com.engine.domain.orderbook.OrderBookManager;
import com.engine.kafka.adapters.KafkaProducerAdapter;
import com.engine.serialization.OrderBookSnapshotSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class OrderBookSnapshotProducer extends KafkaProducerAdapter<OrderBookSnapshot> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookSnapshotProducer.class);
    private final OrderBookManager orderBookManager;
    private final int period = 2;

    public OrderBookSnapshotProducer(final OrderBookManager orderBookManager) {
        this.orderBookManager = orderBookManager;
    }

    public void run() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (OrderBook orderBook : orderBookManager.orderBooks.values()) {
                try {
                    orderBook.withLock(() -> {
                        produce(orderBook.getSnapshot());
                        return null;
                    });
                } catch (Exception e) {
                    LOGGER.info("Failed to get snapshot of Order Book for " + orderBook.getSecurity() + ": " + e.getMessage());
                }
            }
        }, period, period, TimeUnit.SECONDS);
    }

    @Override
    public ProducerRecord<String, String> serialize(OrderBookSnapshot snapshot) {
        try {
            SimpleModule module = new SimpleModule();
            module.addSerializer(OrderBookSnapshot.class, new OrderBookSnapshotSerializer());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(module);

            String json = objectMapper.writeValueAsString(snapshot);
            LOGGER.info("Sending Order Book snapshot for " + snapshot.getSecurity());
            return new ProducerRecord<>("order-book-snapshots", snapshot.getSecurity(), json);
        } catch (IOException e) {
            LOGGER.error("Failed to serialize order book snapshot: " + e.getMessage());
            return null;
        }
    }
}