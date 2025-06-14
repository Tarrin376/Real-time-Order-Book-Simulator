import { Security } from "./Security"

export type Execution = {
    orderId: string,
    cancelOrderId: string,
    id: string,
    side: "BUY" | "SELL",
    security: Security,
    price: number,
    delta: number,
    timestamp: number
}