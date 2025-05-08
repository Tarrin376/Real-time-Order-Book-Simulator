import { Socket } from "socket.io-client";
import Metric from "./Metric";
import { Security } from "../../types/Security";
import { useCallback, useEffect, useState } from "react";
import { MetricData, Snapshot } from "../../types/Snapshot";

interface MetricsProps {
    socket: Socket | undefined,
    security: Security
}

function Metrics({ socket, security }: MetricsProps) {
    const [metricData, setMetricData] = useState<MetricData>();
    
    const handleSnapshot = useCallback((snapshot: Snapshot) => {
        setMetricData(snapshot.metrics);
    }, []);

    useEffect(() => {
        if (!socket) {
            return;
        }
        
        setMetricData(undefined);
        socket.on(`snapshot-${security}`, handleSnapshot);
        
        return () => {
            socket.off(`snapshot-${security}`, handleSnapshot);
        }
    }, [socket, security, handleSnapshot]);
    
    return (
        <div className="info-section-wrapper">
            <Metric 
                title="Spread" 
                value={metricData?.spread ?? "-"} 
            />
            <Metric 
                title="Best Bid" 
                value={metricData?.bestBid ?? "-"} 
                styles="buy-price"
            />
            <Metric 
                title="Best Ask" 
                value={metricData?.bestAsk ?? "-"} 
                styles="sell-price"
            />
            <Metric 
                title="Liquidity Ratio (Bid/Ask)" 
                value={metricData?.liquidityRatio ?? "-"} 
            />
            <Metric 
                title="Total Volume" 
                value={metricData?.totalVolume ?? "-"} 
            />
        </div>
    )
}

export default Metrics;