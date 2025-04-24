import time
import random
import uuid

class TradingClient:
    _tickers = ["AAPL", "TSLA", "META", "GOOG"]
    _order_types = ["LIMIT", "MARKET"]

    def run(self):
        while True:
            order = self.generate_order()
            self.send_order(order)
            time.sleep(0.3)
        
    def generate_order(self):
        return {
            "type": random.choice(self._order_types),
            "side": random.choice(["BUY", "SELL"]),
            "symbol": random.choice(self._tickers),
            "price": random.random() * 200,
            "quantity": random.randrange(1, 100),
            "order_id": str(uuid.uuid4())
        }

    def send_order(self, order):
        print(order)

if __name__ == "__main__":
    TradingClient().run()