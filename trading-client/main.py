from confluent_kafka import Producer
from random import random, choice, randrange
from uuid import uuid4

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
    order_types = ["LIMIT"]
    securities = {"AAPL": 100, "TSLA": 200, "META": 300, "GOOG": 400}
    volatility = 4

    def __init__(self):
        self.logger = logging.getLogger('trading-client')
        self.producer = self.get_kafka_producer()
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
            order = self.generate_order()
            self.send_order(order)
            time.sleep(0.3)
        
    def generate_order(self):
        security = choice(list(self.securities.keys()))
        return {
            "type": choice(self.order_types),
            "side": choice(["BUY", "SELL"]),
            "security": security,
            "price": "%.2f" % (self.securities[security] + (random() * 2 - 1) * self.volatility),
            "quantity": randrange(1, 100),
            "orderId": str(uuid4()),
            "timestamp": time.time()
        }

    def timestampToString(self, timestamp):
        return time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(timestamp))

    def send_order(self, order):
        self.logger.info(f"Sending order: [{order['orderId']}] {order['type']} {order['side']} " + 
                        f"{order['security']} | £{order['price']} {order['quantity']}x ({self.timestampToString(order['timestamp'])})")
        self.producer.produce('orders', key=order['security'], value=json.dumps(order).encode('utf-8'))

if __name__ == "__main__":
    TradingClient()