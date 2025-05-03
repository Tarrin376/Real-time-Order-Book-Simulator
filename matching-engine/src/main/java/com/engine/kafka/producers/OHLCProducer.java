package com.engine.kafka.producers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.engine.domain.model.Execution;
import com.engine.domain.model.OHLC;
import com.engine.kafka.adapters.KafkaProducerAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OHLCProducer extends KafkaProducerAdapter<OHLC> {
    private final ConcurrentHashMap<String, List<Execution>> securityBuffers;
    private static final Logger LOGGER = LoggerFactory.getLogger(OHLCProducer.class);
    private final int flushPeriod = 10;

    public OHLCProducer() {
        this.securityBuffers = new ConcurrentHashMap<>();
    }

    public void addToBuffer(final Execution exec) {
        String security = exec.getSecurity();
        if (!securityBuffers.containsKey(security)) {
            securityBuffers.put(security, Collections.synchronizedList(new ArrayList<>()));
        }

        securityBuffers.get(security).add(exec);
    }

    public final void run() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (Map.Entry<String, List<Execution>> pair : securityBuffers.entrySet()) {
                String ticker = pair.getKey();
                List<Execution> buffer = pair.getValue();

                synchronized (buffer) {
                    if (buffer.isEmpty()) {
                        return;
                    }

                    BigDecimal open = getOpen(buffer);
                    BigDecimal high = getHigh(buffer);
                    BigDecimal low = getLow(buffer);
                    BigDecimal close = getClose(buffer);

                    OHLC ohlc = new OHLC(open, high, low, close, ticker, getStartTimestamp(buffer), getEndTimestamp(buffer));
                    LOGGER.info("Sending OHLC: " + ohlc);

                    produce(ohlc);
                    buffer.clear();
                }
            }
        }, flushPeriod, flushPeriod, TimeUnit.SECONDS);
    }

    private BigDecimal getOpen(final List<Execution> buffer) {
        return buffer.get(0).getPrice();
    }

    private BigDecimal getHigh(final List<Execution> buffer) {
        return buffer
            .stream()
            .max((a, b) -> a.getPrice().compareTo(b.getPrice()))
            .get()
            .getPrice();
    }

    private BigDecimal getLow(final List<Execution> buffer) {
        return buffer
            .stream()
            .min((a, b) -> a.getPrice().compareTo(b.getPrice()))
            .get()
            .getPrice();
    }

    private BigDecimal getClose(final List<Execution> buffer) {
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
            return new ProducerRecord<>("ohlc-events", ohlc.getSecurity(), json);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to serialize ohlc event: " + e.getMessage());
            return null;
        }
    }
}