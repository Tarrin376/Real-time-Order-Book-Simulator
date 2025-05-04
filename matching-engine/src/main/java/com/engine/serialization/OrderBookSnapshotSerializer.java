package com.engine.serialization;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import com.engine.domain.model.Order;
import com.engine.domain.model.OrderBookSnapshot;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class OrderBookSnapshotSerializer extends StdSerializer<OrderBookSnapshot> {
    public OrderBookSnapshotSerializer() {
        this(null);
    }

    public OrderBookSnapshotSerializer(final Class<OrderBookSnapshot> snapshot) {
        super(snapshot);
    }

    @Override
    public void serialize(final OrderBookSnapshot snapshot, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("security", snapshot.getSecurity());
        gen.writeNumberField("seqId", snapshot.getSeqId());

        gen.writeArrayFieldStart("bids");
        writePriceLevels(snapshot.bids, gen);
        gen.writeEndArray();

        gen.writeArrayFieldStart("asks");
        writePriceLevels(snapshot.asks, gen);
        gen.writeEndArray();

        gen.writeEndObject();
    }

    private void writePriceLevels(final TreeMap<BigDecimal, TreeSet<Order>> levels, final JsonGenerator gen) throws IOException {
        for (Map.Entry<BigDecimal, TreeSet<Order>> entry : levels.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                String price = entry.getKey().toPlainString();
                gen.writeStartObject();
                gen.writeNumberField("count", entry.getValue().size());
                gen.writeNumberField("amount", entry.getValue().stream().reduce(0, (amount, order) -> amount + order.getQuantity(), Integer::sum));
                gen.writeStringField("price", price);
                gen.writeEndObject();
            }
        }
    }
}