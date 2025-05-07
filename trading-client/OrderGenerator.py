import random
import time
from uuid import uuid4

class OrderGenerator:
    order_types = ["LIMIT", "MARKET", "CANCEL"]
    generated_orders = {}

    securities = {"AAPL": 100, "TSLA": 200, "META": 300, "GOOG": 400}
    price_anchor = {security: random.uniform(100, 500) for security in securities}

    volatility = 1.5 
    trend_drift = 0.05
    buy_bias = 0.5

    def __init__(self):
        for security in self.securities.keys():
            self.generated_orders[security] = []

    def generate_order(self):
        self.flush_prev_generated_orders()

        security = random.choice(list(self.securities.keys()))
        order_type = random.choice(self.order_types)
        order = {}

        if order_type == "LIMIT" or order_type == "MARKET":
            order = self.generate_market_or_limit_order(order_type, security)
            self.generated_orders[security].append(order.get("orderId"))
        elif order_type == "CANCEL":
            order = self.generate_cancel_order(order_type, security)

        return order

    def flush_prev_generated_orders(self):
        flush_threshold = 50
        for security in self.securities.keys():
            if len(self.generated_orders[security]) >= flush_threshold:
                self.generated_orders[security] = []

    def generate_cancel_order(self, order_type, security):
        if len(self.generated_orders[security]) == 0:
            return None
        
        return {
            "type": order_type,
            "security": security,
            "orderId": str(uuid4()),
            "cancelOrderId": random.choice(self.generated_orders[security]),
            "timestamp": time.time()
        }
    
    def generate_random_price(self, security):
        drift = self.trend_drift if random.random() < 0.5 else -self.trend_drift
        self.price_anchor[security] += drift
        noise = random.gauss(0, self.volatility)
        return round(self.price_anchor[security] + noise, 2)

    def generate_market_or_limit_order(self, order_type, security):
        order = {
            "type": order_type,
            "side": random.choice(["BUY", "SELL"]),
            "security": security,
            "quantity": random.randrange(1, 1000),
            "orderId": str(uuid4()),
            "timestamp": time.time()
        }

        if order_type == "LIMIT":
            order['price'] = "%.2f" % self.generate_random_price(security)
        
        return order
