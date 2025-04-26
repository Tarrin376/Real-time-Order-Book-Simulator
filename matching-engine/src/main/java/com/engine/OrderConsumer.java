package com.engine;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.engine.dto.OrderDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OrderConsumer {
    private final String bootstrapServers = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
    private final Properties properties = new Properties() {{
        put("bootstrap.servers", bootstrapServers);
        put("key.deserializer", StringDeserializer.class.getCanonicalName());
        put("value.deserializer", StringDeserializer.class.getCanonicalName());
        put("group.id", "matching-engine");
        put("auto.offset.reset", "earliest");
    }};

    public void listen() {
        try (final Consumer<String, String> consumer = new KafkaConsumer<>(properties)) {
            consumer.subscribe(Arrays.asList("orders"));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    OrderDto order = parseOrder(record.value());
                    if (order != null) {
                        System.out.println(order);
                    }
                }
            }
        }
    }

    private OrderDto parseOrder(final String orderJSON) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(orderJSON, OrderDto.class);
        } catch (JsonProcessingException e) {
            System.out.println("Failed to read order: " + orderJSON);
            return null;
        }
    }
}
