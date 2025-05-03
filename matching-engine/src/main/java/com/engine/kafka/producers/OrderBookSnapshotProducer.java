package com.engine.kafka.producers;

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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class OrderBookSnapshotProducer extends KafkaProducerAdapter<OrderBookSnapshot> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookSnapshotProducer.class);
    private final OrderBookManager orderBookManager;
    private final int period = 5;

    public OrderBookSnapshotProducer(final OrderBookManager orderBookManager) {
        this.orderBookManager = orderBookManager;
    }

    public void run() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (OrderBook orderBook : orderBookManager.orderBooks.values()) {
                OrderBookSnapshot snapshot = orderBook.getSnapshot();
                produce(snapshot);
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
            return new ProducerRecord<>("order-book-snapshots", snapshot.getSecurity(), json);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to serialize order book snapshot: " + e.getMessage());
            return null;
        }
    }
}