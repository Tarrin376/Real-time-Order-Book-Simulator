import { Security } from "./Security"

export type OHLC = {
    open: number,
    high: number,
    low: number,
    close: number,
    security: Security,
    startTimestamp: number,
    endTimestamp: number
}