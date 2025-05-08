package com.engine.kafka.producers;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engine.domain.model.OrderExecution;
import com.engine.kafka.adapters.KafkaProducerAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExecutionProducer extends KafkaProducerAdapter<OrderExecution> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionProducer.class);
    private final OpenHighLowCloseProducer ohlcDataAggregator;

    public ExecutionProducer(final OpenHighLowCloseProducer ohlcDataAggregator) {
        this.ohlcDataAggregator = ohlcDataAggregator;
        ohlcDataAggregator.run();
    }

    public void sendExecution(final OrderExecution exec) {
        ohlcDataAggregator.addToBuffer(exec);
        produce(exec);
    }

    @Override
    public ProducerRecord<String, String> serialize(final OrderExecution exec) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(exec);
            LOGGER.info("Executed: " + exec);
            return new ProducerRecord<>("executions", exec.getSecurity(), json);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to serialize execution: " + e.getMessage());
            return null;
        }
    }
}