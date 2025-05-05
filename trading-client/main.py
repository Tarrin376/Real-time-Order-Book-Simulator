from confluent_kafka import Producer
from uuid import uuid4
from OrderGenerator import OrderGenerator

import random
import time
import socket
import json
import logging
import sys
import os

conf = {'bootstrap.servers': os.getenv('KAFKA_BOOTSTRAP_SERVERS', 'localhost:9092'),
        'client.id': socket.gethostname()}

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[logging.StreamHandler(sys.stdout)]
)

sys.stdout.reconfigure(line_buffering=True)

class TradingClient:
    def __init__(self):
        self.logger = logging.getLogger('trading-client')
        self.producer = self.get_kafka_producer()
        self.order_generator = OrderGenerator()
        self.run()

    def get_kafka_producer(self):
        try:
            self.logger.info("Connecting to Kafka broker...")
            producer = Producer(conf)
            self.logger.info("Connected to Kafka broker successfully")
            return producer
        except Exception as e:
            self.logger.error(f"Failed to connect to Kafka broker: {e}")
            sys.exit(1)

    def run(self):
        self.logger.info("Trading client started, beginning to generate orders...")
        
        while True:
            order = self.order_generator.generate_order()
            if order != None:
                self.send_order(order)
                time.sleep(0.1)

    def timestamp_to_string(self, timestamp):
        return time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(timestamp))

    def send_order(self, order):
        price = f"£{order.get('price')} " if order.get('price') != None else ""
        quantity = f"(x{order.get('quantity')}) | " if order.get('quantity') != None else ""

        if order['type'] == "CANCEL":
            self.logger.info(f"Order ID: [{order['orderId']}] " + f"{order['type']} {order['cancelOrderId']} {self.timestamp_to_string(order['timestamp'])}")
        else:
            self.logger.info(f"Order ID: [{order['orderId']}] " + f"{order['type']} {order['side']} " + 
                            f"{order['security']} | " + price + quantity + f"{self.timestamp_to_string(order['timestamp'])}")
        
        self.producer.produce('orders', key=order['security'], value=json.dumps(order).encode('utf-8'))

if __name__ == "__main__":
    TradingClient()