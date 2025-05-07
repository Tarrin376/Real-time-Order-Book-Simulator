import { Security } from "./Security"

export type Snapshot = {
    metrics: MetricData,
    bids: Level[],
    asks: Level[]
}

export type Level = {
    count: number,
    amount: number,
    price: string
}

export type MetricData = {
    security: Security,
    spread: string,
    bestBid: string,
    bestAsk: string,
    liquidityRatio: string,
    totalVolume: string,
}