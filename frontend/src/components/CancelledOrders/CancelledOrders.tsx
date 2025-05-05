import { useState } from "react";
import { Execution } from "../../types/Execution";
import { Socket } from "socket.io-client";
import { Security } from "../../types/Security";

interface CancelledOrdersProps {
    socket: Socket | undefined,
    security: Security
}

function CancelledOrders({ socket, security }: CancelledOrdersProps) {
    const [cancelledOrders, setCancelledOrders] = useState<Execution[]>([]);
    
    return (
        <div className="cancelled-orders component">
            <h2>Cancelled Orders</h2>
        </div>
    )
}

export default CancelledOrders;