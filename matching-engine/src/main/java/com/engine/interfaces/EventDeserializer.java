package com.engine.interfaces;

public interface EventDeserializer<T> {
    T deserialize(final String json);
}