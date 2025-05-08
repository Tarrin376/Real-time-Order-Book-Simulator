import express from 'express';
import http from 'http';

import { consume } from './kafka/kafkaConsumer.js';
import { Server } from 'socket.io';
import cors from 'cors';

const app = express();
app.use(cors({ 
    origin: 'http://localhost:9000', 
}));

const server = http.createServer(app);
const io = new Server(server, {
    cors: { 
        origin: 'http://localhost:9000',
    }
});

function handleExecution(execution) {
    const execObj = JSON.parse(execution);
    io.emit(`execution-${execObj['security']}`, execObj);
}

function handleOHLCEvent(ohlcEvent) {
    const ohlcObj = JSON.parse(ohlcEvent);
    io.emit(`ohlc-${ohlcObj['security']}`, ohlcObj);
}

function handleOrderBookSnapshot(snapshot) {
    const snapshotObj = JSON.parse(snapshot);
    io.emit(`snapshot-${snapshotObj['security']}`, snapshotObj);
}

consume({
    onExecution: handleExecution,
    onOHLCEvent: handleOHLCEvent,
    onOrderBookSnapshot: handleOrderBookSnapshot
});

server.listen(3000, () => {
    console.log("Event gateway listening on port 3000");
});