package com.engine.kafka;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.engine.interfaces.EventDeserializer;
import com.engine.interfaces.EventHandler;

public class KafkaConsumerAdapter<T> {
    private final Properties properties;
    private final EventHandler<T> eventHandler;
    private final EventDeserializer<T> eventDeserializer;
    private final List<String> topics;

    public KafkaConsumerAdapter(final Properties properties, final EventHandler<T> eventHandler, 
        final EventDeserializer<T> eventDeserializer, final List<String> topics) {
        this.properties = properties;
        this.eventHandler = eventHandler;
        this.eventDeserializer = eventDeserializer;
        this.topics = topics;
    }

    public void consume() {
        try (final Consumer<String, String> consumer = new KafkaConsumer<>(properties)) {
            consumer.subscribe(topics);
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    final T deserializedEvent = eventDeserializer.deserialize(record.value());
                    eventHandler.handle(deserializedEvent);
                }
            }
        }
    }
}
