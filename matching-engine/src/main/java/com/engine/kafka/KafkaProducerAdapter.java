package com.engine.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import com.engine.interfaces.EventSerializer;

public class KafkaProducerAdapter<T> {
    private final Properties properties;
    private final EventSerializer<T> eventSerializer;

    public KafkaProducerAdapter(final Properties properties, final EventSerializer<T> eventSerializer) {
        this.properties = properties;
        this.eventSerializer = eventSerializer;
    }

    public void produce(final T event) {
        try (final Producer<String, String> producer = new KafkaProducer<>(properties)) {
            producer.send(eventSerializer.serialize(event));
        }
    }
}