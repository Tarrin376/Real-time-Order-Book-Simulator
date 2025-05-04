import pkg from '@confluentinc/kafka-javascript';
import dotenv from 'dotenv';

const { KafkaJS } = pkg;
dotenv.config();

const consumer = new KafkaJS.Kafka().consumer({
    'bootstrap.servers': process.env.KAFKA_BOOTSTRAP_SERVERS ?? 'localhost:9092',
    'group.id': 'event-gateway',
    'auto.offset.reset': 'latest'
});

const topics = ["executions", "ohlc-events", "order-book-snapshots"];

export async function consume({ onExecution, onOHLCEvent, onOrderBookSnapshot }) {
    try {
        await consumer.connect();
        console.log("Kafka consumer connected successfully");

        await consumer.subscribe({topics: topics});
        console.log(`Subscribed to topics: ${topics.join(", ")}`);

        await consumer.run({
            eachMessage: async ({ topic, _, message }) => {
                try {
                    const value = message.value.toString();
                    switch (topic) {
                        case "executions": 
                            onExecution(value);
                            break;
                        case "ohlc-events": 
                            onOHLCEvent(value);
                            break;
                        case "order-book-snapshots":
                            onOrderBookSnapshot(value);
                            break;
                        default: 
                            console.log(`Received event from unhandled topic: ${topic}`);
                            break;
                    }
                } catch (err) {
                    console.error("Error processing message:", err);
                }
            }
        });
    } catch (err) {
        console.error("Error in Kafka consumer: ", err);
    }
}