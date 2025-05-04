import { useEffect, useState } from "react";
import { Security } from "../../types/Security";
import { Socket } from "socket.io-client";
import { Execution } from "../../types/Execution";
import { formatTimestampToTime } from "../../utils/dateFormats";

interface MarketProps {
    socket: Socket | undefined,
    security: Security
}

function Market({ socket, security }: MarketProps) {
    const [executions, setExecutions] = useState<Execution[]>([]);
    const maxSize = 45;
    
    function handleExecution(execution: Execution) {
        setExecutions((recent) => [execution, ...recent.slice(0, Math.min(recent.length, maxSize - 1))]);
    }

    useEffect(() => {
        if (!socket) {
            return;
        }

        setExecutions([]);
        socket.on(`execution-${security}`, handleExecution);
        
        return () => {
            socket.off(`execution-${security}`, handleExecution);
        }
    }, [socket, security]);
    
    return (
        <div className="market component">
            <h2>Market</h2>
            <table className="market-table">
                <thead>
                    <tr className="market-table-header">
                        <th>Price (GBP)</th>
                        <th>Amount</th>
                        <th>Time</th>
                    </tr>
                </thead>
                <tbody>
                    {executions.map(execution => {
                        return (
                            <tr className="market-table-data" key={execution.id}>
                                <td className={execution.side == "BUY" ? "buy-price" : "sell-price"}>{execution.price}</td>
                                <td>{execution.delta}</td>
                                <td>{formatTimestampToTime(execution.timestamp)}</td>
                            </tr>
                        )
                    })}
                </tbody>
            </table>
        </div>
    )
}

export default Market;