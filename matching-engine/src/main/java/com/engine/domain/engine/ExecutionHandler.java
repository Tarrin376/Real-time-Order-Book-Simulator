package com.engine.domain.engine;

import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import com.engine.domain.model.Execution;
import com.engine.interfaces.EventSerializer;
import com.engine.kafka.KafkaProducerAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExecutionHandler implements EventSerializer<Execution> {
    private final String bootstrapServers = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
    private final Properties properties = new Properties() {{
        put("bootstrap.servers", bootstrapServers);
        put("key.serializer", StringSerializer.class.getCanonicalName());
        put("value.serializer", StringSerializer.class.getCanonicalName());
        put("acks", "all");
    }};

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
            System.out.println("Executed: " + json);
            return new ProducerRecord<>("executions", exec.getTicker(), json);
        } catch (JsonProcessingException e) {
            System.out.println("Failed to serialize execution");
            return null;
        }
    }
}