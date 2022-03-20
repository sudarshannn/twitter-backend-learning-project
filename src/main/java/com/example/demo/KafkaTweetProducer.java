package com.example.demo;


import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.TimeUnit;


// this producer code will fetch tweets from twitter using hbc client and stream those tweets into kafka-topics
public class KafkaTweetProducer implements Runnable {
    private TwitterClient twitterClient;
    private KafkaProducer<String ,String > producer;

    Logger logger = LoggerFactory.getLogger(KafkaTweetProducer.class.getName());


    public KafkaTweetProducer() {
        twitterClient = new TwitterClient();
    }

    public void run() {
        twitterClient.Connect();
        producer = getNewProducer();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping Fetching Tweets");
            twitterClient.Stop();
            producer.close();
            logger.info("Closing Producer");
            ElasticSearchClient.Close();
            logger.info("Closing ElasticSearch");
            RedisClient.Close();
            logger.info("closing Redis");
        }));

        while(!twitterClient.getClient().isDone()) {
            String tweetObject =null;
            try {
                tweetObject = twitterClient.getMsgQueue().poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                twitterClient.Stop();
            }
            if(tweetObject != null) {
                logger.info(tweetObject);
                producer.send(new ProducerRecord<>(KafkaProperties.TOPIC, null, tweetObject), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        if(e != null) {
                            logger.error("something went wrong: KafkaProducer",e);
                        }
                    }
                });
            }
        }
    }

    public KafkaProducer<String ,String > getNewProducer() {
        Properties prop = new Properties();
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,KafkaProperties.IP);
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<String,String>(prop);
    }
}
