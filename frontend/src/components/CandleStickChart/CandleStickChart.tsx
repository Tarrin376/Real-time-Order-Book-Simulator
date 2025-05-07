import { Socket } from "socket.io-client";
import { Security } from "../../types/Security";
import { useCallback, useEffect, useRef, useState } from "react";
import { createChart, ColorType, ISeriesApi, CandlestickData, UTCTimestamp } from "lightweight-charts";
import { OHLC } from "../../types/OHLC";

interface CandleStickChartProps {
    socket: Socket | undefined,
    security: Security
}

function CandleStickChart({ socket, security }: CandleStickChartProps) {
    const [ohlcEvents, setOhlcEvents] = useState<CandlestickData[]>([]);
    const candlestickSeriesRef = useRef<ISeriesApi<'Candlestick'> | null>(null);
    const chartRef = useRef<HTMLDivElement>(null);

    const handleOHLCEvent = useCallback((ohlcEvent: OHLC) => {
        setOhlcEvents((cur) => [...cur, {
            ...ohlcEvent,
            time: Math.floor(ohlcEvent.timestamp) as UTCTimestamp
        }]);
    }, [setOhlcEvents]);

    useEffect(() => {
        if (!chartRef?.current || !socket) {
            return;
        }

        socket.on(`ohlc-${security}`, handleOHLCEvent);
        setOhlcEvents([]);

        const handleResize = () => {
            chart.applyOptions({ 
                width: chartRef.current?.clientWidth,
                height: chartRef.current?.clientHeight
            });
        };

        const chart = createChart(chartRef.current, {
            width: chartRef.current.clientWidth,
            height: chartRef.current.clientHeight,
            layout: {
                background: { type: ColorType.Solid, color: '#202020' },
                textColor: 'rgb(219, 171, 161)',
            },
            grid: {
                vertLines: { color: '#343434' },
                horzLines: { color: '#343434' },
            },
            timeScale: { 
                borderColor: '#444',
                timeVisible: true,
                secondsVisible: true,
                barSpacing: 15
            },
            rightPriceScale: {
                scaleMargins: {
                  top: 0.1,
                  bottom: 0.1
                }
            }
        });

        const candlestickSeries = chart.addCandlestickSeries({
            priceScaleId: 'right',
            upColor: '#4caf50',
            downColor: '#e53552',
            borderVisible: false,
            wickUpColor: '#4caf50',
            wickDownColor: '#e53552',
        });

        candlestickSeriesRef.current = candlestickSeries;
        candlestickSeries.setData([]);
        window.addEventListener('resize', handleResize);

        return () => {
            window.removeEventListener('resize', handleResize);
            socket.off(`ohlc-${security}`, handleOHLCEvent);
            chart.remove();
        };
    }, [socket, security, handleOHLCEvent]);

    useEffect(() => {
        const series = candlestickSeriesRef.current;
        if (!series || ohlcEvents.length === 0) {
            return;
        }

        const lastBar = ohlcEvents[ohlcEvents.length - 1];
        series.update(lastBar);
    }, [ohlcEvents]);

    return (
        <div className="chart-container component" ref={chartRef}>
        </div>
    )
}

export default CandleStickChart;