package com.engine.domain.engine;

import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engine.domain.model.Execution;
import com.engine.interfaces.EventSerializer;
import com.engine.kafka.KafkaProducerAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExecutionHandler implements EventSerializer<Execution> {
    private final Properties properties = new Properties() {{
        put("bootstrap.servers", System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"));
        put("key.serializer", StringSerializer.class.getCanonicalName());
        put("value.serializer", StringSerializer.class.getCanonicalName());
        put("acks", "all");
    }};

    private static final Logger logger = LoggerFactory.getLogger(ExecutionHandler.class);
    private final KafkaProducerAdapter<Execution> producerAdapter;
    private final OHLCDataAggregator ohlcDataAggregator;

    public ExecutionHandler() {
        this.producerAdapter = new KafkaProducerAdapter<>(properties, this);
        this.ohlcDataAggregator = new OHLCDataAggregator(properties);
    }

    public void sendExecution(final Execution exec) {
        ohlcDataAggregator.addToBuffer(exec);
        producerAdapter.produce(exec);
    }

    @Override
    public ProducerRecord<String, String> serialize(final Execution exec) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(exec);
            logger.info("Executed: " + exec);
            return new ProducerRecord<>("executions", exec.getSecurity(), json);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize execution: " + e.getMessage());
            return null;
        }
    }
}