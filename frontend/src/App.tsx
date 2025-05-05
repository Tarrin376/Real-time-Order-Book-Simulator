import './App.css';
import io, { Socket } from 'socket.io-client';
import { useEffect, useState } from 'react';
import CandleStickChart from './components/CandleStickChart/CandleStickChart';
import SecurityInfo from './components/SecurityInfo/SecurityInfo';
import { Security } from './types/Security';
import Market from './components/Market/Market';
import OrderBook from './components/OrderBook/OrderBook';
import CancelledOrders from './components/CancelledOrders/CancelledOrders';

function App() {
    const [socket, setSocket] = useState<Socket>();
    const [security, setSecurity] = useState<Security>("AAPL");

    function changeSecurity(nextSecurity: Security) {
        setSecurity(nextSecurity);
    }

    useEffect(() => {
        const ws = io('http://localhost:3000');
        setSocket(ws);
        return () => {
            ws?.disconnect();
        }
    }, []);

    return (
        <div className="main-page">
            <div className="info-wrapper">
                <div className="security-wrapper">
                    <SecurityInfo 
                        socket={socket} 
                        security={security} 
                        changeSecurity={changeSecurity} 
                    />
                    <CandleStickChart 
                        socket={socket} 
                        security={security} 
                    />
                </div>
                <OrderBook 
                    socket={socket} 
                    security={security} 
                />
            </div>
            <div className="executions-wrapper">
                <Market 
                    socket={socket} 
                    security={security} 
                />
                <CancelledOrders
                    socket={socket}
                    security={security}
                />
            </div>
        </div>
    )
}

export default App
