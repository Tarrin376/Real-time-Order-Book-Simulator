package com.engine.domain.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.engine.domain.model.Execution;

public class CandlestickAggregator {
    private final List<Execution> buffer;

    public CandlestickAggregator() {
        this.buffer = Collections.synchronizedList(new ArrayList<>());
        scheduleFlush();
    }

    public void add(Execution exec) {
        buffer.add(exec);
    }

    public final void scheduleFlush() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            List<Execution> batch;

            synchronized (buffer) {
                if (buffer.isEmpty()) return;
                batch = new ArrayList<>(buffer);
                buffer.clear();
            }

            // compute Open/Close and Low/High (to be consumed by 'candlestick-chart' topic)
        }, 1, 1, TimeUnit.SECONDS);
    }
}