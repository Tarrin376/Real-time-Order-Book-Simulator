package com.engine.interfaces;

import org.apache.kafka.clients.producer.ProducerRecord;

public interface EventSerializer<T> {
    ProducerRecord<String, String> serialize(T event);
}