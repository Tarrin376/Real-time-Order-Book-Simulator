import { Socket } from 'socket.io-client';
import { Security } from '../../types/Security';
import { useCallback, useEffect, useState } from 'react';
import { Snapshot } from '../../types/Snapshot';
import { Execution } from '../../types/Execution';

interface OrderBookProps {
    socket: Socket | undefined,
    security: Security
}

function OrderBook({ socket, security }: OrderBookProps) {
    const [initialSnapshot, setInitialSnapshot] = useState<Snapshot>();

    const handleSnapshot = useCallback((snapshot: Snapshot) => {
        if (initialSnapshot && initialSnapshot.security == snapshot.security) {
            socket?.off(`snapshot-${security}`, handleSnapshot);
            return;
        }

        setInitialSnapshot(snapshot);
    }, [security]);

    const handleExecution = useCallback((execution: Execution) => {
        if (!initialSnapshot || execution.seqId <= initialSnapshot.seqId) {
            return;
        }

        console.log(execution);
    }, [security]);

    useEffect(() => {
        if (!socket) {
            return;
        }

        socket.on(`snapshot-${security}`, handleSnapshot);
        socket.on(`execution-${security}`, handleExecution);

        return () => {
            socket.off(`snapshot-${security}`, handleSnapshot);
            socket.off(`execution-${security}`, handleExecution);
        }
    }, [socket, security]);

    return (
        <div className="component order-book">
            <h2>Order Book</h2>
        </div>
    )
}

export default OrderBook;