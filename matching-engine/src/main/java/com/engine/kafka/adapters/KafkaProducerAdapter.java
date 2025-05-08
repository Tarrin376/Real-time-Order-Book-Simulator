package com.engine.kafka.adapters;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public abstract class KafkaProducerAdapter<T> {
    private final Properties properties = new Properties() {{
        put("bootstrap.servers", System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"));
        put("key.serializer", StringSerializer.class.getCanonicalName());
        put("value.serializer", StringSerializer.class.getCanonicalName());
        put("acks", "all");
    }};

    public final void produce(final T event) {
        try (final Producer<String, String> producer = new KafkaProducer<>(properties)) {
            producer.send(serialize(event));
        }
    }

    public abstract ProducerRecord<String, String> serialize(final T event);
}