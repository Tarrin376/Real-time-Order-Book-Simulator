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

import com.engine.domain.model.OrderExecution;
import com.engine.domain.model.OpenHighLowClose;
import com.engine.kafka.adapters.KafkaProducerAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OpenHighLowCloseProducer extends KafkaProducerAdapter<OpenHighLowClose> {
    private final ConcurrentHashMap<String, List<OrderExecution>> securityBuffers;
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenHighLowCloseProducer.class);
    private final int flushPeriod = 20;

    public OpenHighLowCloseProducer() {
        this.securityBuffers = new ConcurrentHashMap<>();
    }

    public void addToBuffer(final OrderExecution exec) {
        String security = exec.getSecurity();
        if (!securityBuffers.containsKey(security)) {
            securityBuffers.put(security, Collections.synchronizedList(new ArrayList<>()));
        }

        securityBuffers.get(security).add(exec);
    }

    public final void run() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (Map.Entry<String, List<OrderExecution>> pair : securityBuffers.entrySet()) {
                List<OrderExecution> buffer = pair.getValue();
                String security = pair.getKey();

                synchronized (buffer) {
                    if (buffer.isEmpty()) {
                        return;
                    }

                    BigDecimal open = getOpen(buffer);
                    BigDecimal high = getHigh(buffer);
                    BigDecimal low = getLow(buffer);
                    BigDecimal close = getClose(buffer);

                    OpenHighLowClose ohlc = new OpenHighLowClose(open, high, low, close, security, getTimestamp(buffer));
                    LOGGER.info("Sending OHLC: " + ohlc);

                    produce(ohlc);
                    buffer.clear();
                }
            }
        }, flushPeriod, flushPeriod, TimeUnit.SECONDS);
    }

    private BigDecimal getOpen(final List<OrderExecution> buffer) {
        return buffer.get(0).getPrice();
    }

    private BigDecimal getHigh(final List<OrderExecution> buffer) {
        return buffer
            .stream()
            .max((a, b) -> a.getPrice().compareTo(b.getPrice()))
            .get()
            .getPrice();
    }

    private BigDecimal getLow(final List<OrderExecution> buffer) {
        return buffer
            .stream()
            .min((a, b) -> a.getPrice().compareTo(b.getPrice()))
            .get()
            .getPrice();
    }

    private BigDecimal getClose(final List<OrderExecution> buffer) {
        return buffer.get(buffer.size() - 1).getPrice();
    }

    private double getTimestamp(final List<OrderExecution> buffer) {
        return buffer.get(0).getTimestamp();
    }

    @Override
    public ProducerRecord<String, String> serialize(final OpenHighLowClose ohlc) {
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