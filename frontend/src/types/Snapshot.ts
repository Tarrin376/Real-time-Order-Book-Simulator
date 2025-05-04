import { Security } from "./Security"

export type Snapshot = {
    security: Security,
    seqId: number,
    bids: Level[],
    asks: Level[]
}

export type Level = {
    count: number,
    amount: number,
    price: string
}