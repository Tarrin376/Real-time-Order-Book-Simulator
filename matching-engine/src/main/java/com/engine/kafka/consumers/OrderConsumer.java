package com.engine.kafka.consumers;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engine.domain.engine.MatchingEngine;
import com.engine.domain.model.Order;
import com.engine.kafka.adapters.KafkaConsumerAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OrderConsumer extends KafkaConsumerAdapter<Order> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);
    private final MatchingEngine matchingEngine;

    public OrderConsumer(final MatchingEngine matchingEngine) {
        super("matching-engine", "order-client", Arrays.asList("orders"));
        this.matchingEngine = matchingEngine;
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
            LOGGER.error("Failed to read order: " + json);
            return null;
        }
    }
}