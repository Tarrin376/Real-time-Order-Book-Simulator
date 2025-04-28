package com.engine.domain.engine;

import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import com.engine.domain.model.Trade;
import com.engine.interfaces.EventSerializer;
import com.engine.kafka.KafkaProducerAdapter;

public class TradeHandler implements EventSerializer<Trade> {
    private final String bootstrapServers = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
    private final Properties properties = new Properties() {{
        put("bootstrap.servers", bootstrapServers);
        put("key.serializer", StringSerializer.class.getCanonicalName());
        put("value.serializer", StringSerializer.class.getCanonicalName());
        put("acks", "all");
    }};

    private final KafkaProducerAdapter<Trade> producerAdapter;

    public TradeHandler() {
        this.producerAdapter = new KafkaProducerAdapter<>(properties, this);
    }

    public void publishTrade(final Trade trade) {
        producerAdapter.produce(trade);
    }

    @Override
    public ProducerRecord<String, String> serialize(final Trade trade) {
        return new ProducerRecord<>("trades", "exec", "{}");
    }
}