import './App.css';
import io from "socket.io-client";
import { useEffect } from 'react';

function App() {
    function handleExecution(execution: string) {
        console.log(execution);
    }

    function handleOHLCEvent(ohlcEvent: string) {
        console.log(ohlcEvent);
    }

    useEffect(() => {
        const ws = io('http://localhost:3000');
        ws.on('connect', () => {
            console.log("Connected!");
        });

        ws.on('execution', handleExecution);
        ws.on('ohlc-event', handleOHLCEvent);

        return () => {
            ws?.disconnect();
        }
    }, []);

    return (
        <p>yo</p>
    )
}

export default App
