from confluent_kafka import Producer
import socket
import time
import random
import uuid
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
    _tickers = ["AAPL", "TSLA", "META", "GOOG"]
    _order_types = ["LIMIT", "MARKET"]

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
        return {
            "type": random.choice(self._order_types),
            "side": random.choice(["BUY", "SELL"]),
            "ticker": random.choice(self._tickers),
            "price": round(random.random() * 200, 2),
            "quantity": random.randrange(1, 100),
            "order_id": str(uuid.uuid4()),
            "timestamp": time.time()
        }

    def send_order(self, order):
        self.logger.info(f"Sending order: [{order['order_id']}] {order['type']} {order['side']} " + 
                        f"{order['ticker']} | £{order['price']} x{order['quantity']} ({order['timestamp']})")
        self.producer.produce('orders', key=order['ticker'], value=json.dumps(order).encode('utf-8'))

if __name__ == "__main__":
    TradingClient()