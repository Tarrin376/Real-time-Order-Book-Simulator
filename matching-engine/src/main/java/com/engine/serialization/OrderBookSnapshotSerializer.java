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

        gen.writeFieldName("metrics");
        gen.writeStartObject();
        gen.writeStringField("spread", snapshot.getSpread() == null ? "-" : snapshot.getSpread().toPlainString());
        gen.writeStringField("bestBid", snapshot.getBestBid() == null ? "-" : snapshot.getBestBid().toPlainString());
        gen.writeStringField("bestAsk", snapshot.getBestAsk() == null ? "-" : snapshot.getBestAsk().toPlainString());
        gen.writeStringField("liquidityRatio", snapshot.getLiquidityRatio() == null ? "-" : snapshot.getLiquidityRatio());
        gen.writeStringField("totalVolume", "" + snapshot.getTotalVolume() == null ? "-" : "" + snapshot.getTotalVolume());
        gen.writeEndObject();
        
        gen.writeArrayFieldStart("bids");
        writeBids(snapshot.bids, gen);
        gen.writeEndArray();

        gen.writeArrayFieldStart("asks");
        writeAsks(snapshot.asks, gen);
        gen.writeEndArray();

        gen.writeEndObject();
    }

    private void writeAsks(final TreeMap<BigDecimal, TreeSet<Order>> levels, final JsonGenerator gen) throws IOException {
        for (Map.Entry<BigDecimal, TreeSet<Order>> entry : levels.entrySet()) {
            TreeSet<Order> orders = entry.getValue();
            if (!orders.isEmpty()) {
                writeLevel(entry.getKey(), orders, gen);
            }
        }
    }

    private void writeBids(final TreeMap<BigDecimal, TreeSet<Order>> levels, final JsonGenerator gen) throws IOException {
        for (BigDecimal price : levels.descendingKeySet()) {
            TreeSet<Order> orders = levels.get(price);
            if (!orders.isEmpty()) {
                writeLevel(price, orders, gen);
            }
        }
    }

    private void writeLevel(final BigDecimal price, final TreeSet<Order> orders, final JsonGenerator gen) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("count", orders.size());
        gen.writeNumberField("amount", orders.stream().reduce(0, (amount, order) -> amount + order.getQuantity(), Integer::sum));
        gen.writeStringField("price", price.toPlainString());
        gen.writeEndObject();
    }
}