package com.engine.domain.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerRecord;

import com.engine.domain.model.Execution;
import com.engine.domain.model.OHLC;
import com.engine.interfaces.EventSerializer;
import com.engine.kafka.KafkaProducerAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OHLCDataAggregator implements EventSerializer<OHLC> {
    private final KafkaProducerAdapter<OHLC> producerAdapter;
    private final ConcurrentHashMap<String, List<Execution>> buffers;

    public OHLCDataAggregator(final Properties properties) {
        this.buffers = new ConcurrentHashMap<>();
        this.producerAdapter = new KafkaProducerAdapter<>(properties, this);
        scheduleFlush();
    }

    public void addToBuffer(final Execution exec) {
        if (!buffers.containsKey(exec.getTicker())) {
            buffers.put(exec.getTicker(), Collections.synchronizedList(new ArrayList<>()));
        }

        buffers.get(exec.getTicker()).add(exec);
    }

    public final void scheduleFlush() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (Map.Entry<String, List<Execution>> pair : buffers.entrySet()) {
                String ticker = pair.getKey();
                List<Execution> buffer = pair.getValue();

                synchronized (buffer) {
                    if (buffer.isEmpty()) {
                        return;
                    }

                    double open = getOpen(buffer);
                    double high = getHigh(buffer);
                    double low = getLow(buffer);
                    double close = getClose(buffer);

                    OHLC ohlc = new OHLC(open, high, low, close, ticker, getStartTimestamp(buffer), getEndTimestamp(buffer));
                    producerAdapter.produce(ohlc);
                    buffer.clear();
                }
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    private double getOpen(final List<Execution> buffer) {
        return buffer.get(0).getPrice();
    }

    private double getHigh(final List<Execution> buffer) {
        return buffer
            .stream()
            .max((a, b) -> Double.compare(a.getPrice(), b.getPrice()))
            .get()
            .getPrice();
    }

    private double getLow(final List<Execution> buffer) {
        return buffer
            .stream()
            .min((a, b) -> Double.compare(a.getPrice(), b.getPrice()))
            .get()
            .getPrice();
    }

    private double getClose(final List<Execution> buffer) {
        return buffer.get(buffer.size() - 1).getPrice();
    }

    private double getStartTimestamp(final List<Execution> buffer) {
        return buffer.get(0).getTimestamp();
    }

    private double getEndTimestamp(final List<Execution> buffer) {
        return buffer.get(buffer.size() - 1).getTimestamp();
    }

    @Override
    public ProducerRecord<String, String> serialize(final OHLC ohlc) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(ohlc);
            return new ProducerRecord<>("ohlc-events", ohlc.getTicker(), json);
        } catch (JsonProcessingException e) {
            System.out.println("Failed to serialize ohlc event");
            return null;
        }
    }
}