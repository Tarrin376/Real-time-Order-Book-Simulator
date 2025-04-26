const kafka = require('@confluentinc/kafka-javascript');
require("dotenv").config();

function createConsumer(config, onData) {
    const consumer = new kafka.KafkaConsumer(config, {'auto.offset.reset': 'earliest'});

    return new Promise((resolve, _) => {
        consumer
        .on('ready', () => resolve(consumer))
        .on('data', onData);

        consumer.connect();
    });
};

async function consumeExecutions() {
    const config = {
        'bootstrap.servers': process.env.KAFKA_BOOTSTRAP_SERVERS ?? 'localhost:9092',
        'group.id': 'trades'
    };

    const consumer = await createConsumer(config, ({key, value}) => {
        console.log(`Consumed event: key = ${key} value = ${value}`);
    });

    consumer.subscribe(["trades"]);
    consumer.consume();

    process.on('SIGINT', () => {
        console.log('\nDisconnecting consumer ...');
        consumer.disconnect();
    });
}

consumeExecutions();