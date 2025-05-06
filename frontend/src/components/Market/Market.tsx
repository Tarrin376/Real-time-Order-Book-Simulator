import { useEffect, useState } from "react";
import { Security } from "../../types/Security";
import { Socket } from "socket.io-client";
import { Execution } from "../../types/Execution";
import { formatTimestampToTime } from "../../utils/dateFormats";

interface MarketProps {
    socket: Socket | undefined,
    security: Security,
    filterByCancelledOrders?: boolean
}

function Market({ socket, security, filterByCancelledOrders }: MarketProps) {
    const [executions, setExecutions] = useState<Execution[]>([]);
    const maxSize = 24;
    
    function handleExecution(execution: Execution) {
        if (filterByCancelledOrders && !execution.cancelOrderId) {
            return;
        }

        if (executions.length > 0 && executions[0].security !== execution.security) {
            setExecutions([execution]);
        } else {
            setExecutions((recent) => [execution, ...recent.slice(0, Math.min(recent.length, maxSize - 1))]);
        }
    }

    useEffect(() => {
        if (!socket) {
            return;
        }

        socket.on(`execution-${security}`, handleExecution);
        return () => {
            socket.off(`execution-${security}`, handleExecution);
        }
    }, [socket, security]);
    
    return (
        <div className="market component">
            <h2>{filterByCancelledOrders ? "Cancelled Orders" : "Market"}</h2>
            <table className="market-table">
                <thead>
                    <tr className="table-header">
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