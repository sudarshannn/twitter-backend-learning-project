package com.example.demo;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import java.time.Duration;


public class FetchTweet_and_push_to_redis implements Runnable {
    @Override
    public void run() {

        KafkaTweetConsumer kafkaTweetConsumer = new KafkaTweetConsumer("grp1");
        Logger logger = LoggerFactory.getLogger(FetchTweet_and_push_to_redis.class.getName());
        RedisClient redisClient = RedisClient.getInstance();
        KafkaConsumer<String ,String > consumer = kafkaTweetConsumer.getConsumer();

        while(true) {
            ConsumerRecords<String ,String > records = consumer.poll(Duration.ofMillis(100));

            for(ConsumerRecord<String,String> record : records) {

                kafkaTweetConsumer.incr_ReadCount();
                String parentTweetId = kafkaTweetConsumer.getFieldFromTweetObject(record.value(), "in_reply_to_status_id");
                if(!parentTweetId.isEmpty() && !parentTweetId.equals("null"))  continue;    // reply tweet


                String tweet = kafkaTweetConsumer.getFieldFromTweetObject(record.value(),"text");
                if(tweet.isEmpty() || tweet.equals("null"))  continue;      // empty text


                String tweetId = kafkaTweetConsumer.getFieldFromTweetObject(record.value(),"id");
                String created_at = kafkaTweetConsumer.getFieldFromTweetObject(record.value(), "created_at");
                String userObj = kafkaTweetConsumer.getFieldFromTweetObject(record.value(),"user");
                String user = kafkaTweetConsumer.getFieldFromTweetObject(userObj,"name");

                if(kafkaTweetConsumer.isInteresting(tweet,kafkaTweetConsumer.getRegexFilters())) {
                    logger.info(kafkaTweetConsumer.getFieldFromTweetObject(record.value(),"id"));
                    redisClient.addTweetId(kafkaTweetConsumer.getFieldFromTweetObject(record.value(),"id"));
                    ElasticSearchClient.PUT(tweetId,user,tweet,created_at);
                }
            }
        }
    }
}