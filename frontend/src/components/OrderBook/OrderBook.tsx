import { Socket } from 'socket.io-client';
import { Security } from '../../types/Security';
import { useCallback, useEffect, useState } from 'react';
import { Snapshot } from '../../types/Snapshot';
import TopPrices from './TopPrices';

interface OrderBookProps {
    socket: Socket | undefined,
    security: Security
}

function OrderBook({ socket, security }: OrderBookProps) {
    const [initialSnapshot, setInitialSnapshot] = useState<Snapshot>();

    const handleSnapshot = useCallback((snapshot: Snapshot) => {
        setInitialSnapshot(snapshot);
    }, []);

    useEffect(() => {
        if (!socket) {
            return;
        }

        setInitialSnapshot(undefined);
        socket.on(`snapshot-${security}`, handleSnapshot);

        return () => {
            socket.off(`snapshot-${security}`, handleSnapshot);
        }
    }, [socket, security, handleSnapshot]);

    return (
        <div className="component order-book">
            {initialSnapshot &&
            <div className="order-book-sides">
                <TopPrices levels={initialSnapshot.bids} isBidSide={true} />
                <TopPrices levels={initialSnapshot.asks} isBidSide={false} />
            </div>}
        </div>
    )
}

export default OrderBook;