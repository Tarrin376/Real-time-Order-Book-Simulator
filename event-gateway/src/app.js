import express from 'express';
import http from 'http';

import { consume } from './kafka/kafkaConsumer.js';
import { Server } from 'socket.io';

const app = express();
const server = http.createServer(app);

const io = new Server(server, {
    cors: {
        origin: 'http://localhost:9000'
    }
});

io.on('connection', (socket) => {
    console.log("A user connected!");
    socket.on('disconnect', () => {
        console.log("A user disconnected.");
    });
});

server.listen(3000, () => {
    console.log("Event gateway listening on port 3000");
});

consume({
    onTrade: (trade) => console.log("Trade: " + trade),
    onOrderBookUpdate: (orderBookState) => console.log("Order book: " + orderBookState)
});