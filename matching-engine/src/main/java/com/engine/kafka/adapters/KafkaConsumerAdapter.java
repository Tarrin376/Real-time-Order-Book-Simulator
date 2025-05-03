package com.engine.kafka.adapters;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public abstract class KafkaConsumerAdapter<T> {
    private final Properties properties = new Properties() {{
        put("bootstrap.servers", System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"));
        put("key.deserializer", StringDeserializer.class.getCanonicalName());
        put("value.deserializer", StringDeserializer.class.getCanonicalName());
        put("auto.offset.reset", "earliest");
    }};

    private final List<String> topics;

    public KafkaConsumerAdapter(final String consumerGroup, final String clientId, final List<String> topics) {
        properties.put("group.id", consumerGroup);
        properties.put("client.id", clientId);
        this.topics = topics;
    }

    public final void consume() {
        try (final Consumer<String, String> consumer = new KafkaConsumer<>(properties)) {
            consumer.subscribe(topics);
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    final T deserializedEvent = deserialize(record.value());
                    handle(deserializedEvent);
                }
            }
        }
    }

    public abstract void handle(T event);
    public abstract T deserialize(final String event);
}
