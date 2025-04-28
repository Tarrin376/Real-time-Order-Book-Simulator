import './App.css';
import io, { Socket } from "socket.io-client";
import { useEffect, useState } from 'react';

function App() {
    const [socket, setSocket] = useState<Socket>();
    useEffect(() => {
        if (socket) {
            return;
        }

        const ws = io('http://localhost:3000');
        ws.on('connect', () => {
            console.log("Connected!");
        });

        setSocket(socket);
    }, [socket]);

    return (
        <p>yo</p>
    )
}

export default App
