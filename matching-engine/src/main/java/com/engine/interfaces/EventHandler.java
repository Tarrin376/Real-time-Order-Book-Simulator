package com.engine.interfaces;

public interface EventHandler<T> {
    void handle(T event);
}