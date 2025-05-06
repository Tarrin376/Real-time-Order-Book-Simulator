import random
import time
from uuid import uuid4

class OrderGenerator:
    _order_types = ["LIMIT", "MARKET", "CANCEL"]
    _generated_orders = {}

    _securities = {"AAPL": 100, "TSLA": 200, "META": 300, "GOOG": 400}
    _volatility = 150

    def __init__(self):
        for security in self._securities.keys():
            self._generated_orders[security] = []

    def generate_order(self):
        self.flush_prev_generated_orders()

        security = random.choice(list(self._securities.keys()))
        order_type = random.choice(self._order_types)
        order = {}

        if order_type == "LIMIT" or order_type == "MARKET":
            order = self.generate_market_or_limit_order(order_type, security)
            self._generated_orders[security].append(order.get("orderId"))
        elif order_type == "CANCEL":
            order = self.generate_cancel_order(order_type, security)

        return order

    def flush_prev_generated_orders(self):
        flush_threshold = 10000
        for security in self._securities.keys():
            if len(self._generated_orders[security]) >= flush_threshold:
                self._generated_orders[security] = []

    def generate_cancel_order(self, order_type, security):
        if len(self._generated_orders[security]) == 0:
            return None
        
        cancel_order_id = random.choice(self._generated_orders[security])

        return {
            "type": order_type,
            "security": security,
            "orderId": str(uuid4()),
            "cancelOrderId": cancel_order_id,
            "timestamp": time.time()
        }

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
            order['price'] = "%.2f" % (self._securities[security] + random.randrange(0, self._volatility))
        
        return order
