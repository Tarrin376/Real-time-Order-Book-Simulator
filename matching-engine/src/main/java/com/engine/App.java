package com.engine;

public class App {
    public static void main(final String[] args) {
        OrderConsumer consumer = new OrderConsumer();
        consumer.listen();
    }
}