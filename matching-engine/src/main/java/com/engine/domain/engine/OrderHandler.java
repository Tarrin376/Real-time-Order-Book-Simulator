package com.engine.domain.engine;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engine.domain.model.Order;
import com.engine.interfaces.EventDeserializer;
import com.engine.interfaces.EventHandler;
import com.engine.kafka.KafkaConsumerAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OrderHandler implements EventHandler<Order>, EventDeserializer<Order> {
    private final Properties properties = new Properties() {{
        put("bootstrap.servers", System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"));
        put("key.deserializer", StringDeserializer.class.getCanonicalName());
        put("value.deserializer", StringDeserializer.class.getCanonicalName());
        put("group.id", "matching-engine");
        put("client.id", "matching-engine");
        put("auto.offset.reset", "earliest");
    }};

    private static final Logger logger = LoggerFactory.getLogger(OrderHandler.class);
    private final MatchingEngine matchingEngine;

    public OrderHandler(final MatchingEngine matchingEngine) {
        this.matchingEngine = matchingEngine;
    }

    public void consumeOrders() {
        new KafkaConsumerAdapter<>(properties, this, this, Arrays.asList("orders")).consume();
    }

    @Override
    public void handle(final Order order) {
        matchingEngine.processNewOrder(order);
    }

    @Override
    public Order deserialize(final String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, Order.class);
        } catch (JsonProcessingException e) {
            logger.error("Failed to read order: " + json);
            return null;
        }
    }
}