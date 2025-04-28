import pkg from '@confluentinc/kafka-javascript';
import dotenv from 'dotenv';

const { KafkaConsumer } = pkg;
dotenv.config();

function createConsumer(config, onData) {
    const consumer = new KafkaConsumer(config, {'auto.offset.reset': 'earliest'});

    return new Promise((resolve, _) => {
        consumer
        .on('ready', () => resolve(consumer))
        .on('data', onData);

        consumer.connect();
    });
};

export async function consume({ onTrade, onOrderBookUpdate }) {
    const config = {
        'bootstrap.servers': process.env.KAFKA_BOOTSTRAP_SERVERS ?? 'localhost:9092',
        'group.id': 'event-gateway'
    };

    const consumer = await createConsumer(config, ({topic, value}) => {
        if (topic === 'trades') {
            onTrade(value);
        } else if (topic === 'order-book-updates') {
            onOrderBookUpdate(value);
        }
    });

    consumer.subscribe(["trades"]);
    consumer.consume();
}