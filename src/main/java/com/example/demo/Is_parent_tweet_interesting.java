package com.example.demo;


import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;


public class Is_parent_tweet_interesting implements Runnable{
    KafkaTweetConsumer kafkaTweetConsumer = new KafkaTweetConsumer("grp2");
    Logger logger = LoggerFactory.getLogger(Is_parent_tweet_interesting.class.getName());
    RedisClient redisClient = RedisClient.getInstance();
    KafkaConsumer<String ,String > consumer = kafkaTweetConsumer.getConsumer();

    @Override
    public void run() {

        while(true ) {
            ConsumerRecords<String ,String > records = consumer.poll(Duration.ofMillis(100));

            for(ConsumerRecord<String,String> record : records) {

                while (kafkaTweetConsumer.getReadCount()<=0)
                {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                kafkaTweetConsumer.decr_Readcount();

                String parentTweetId = kafkaTweetConsumer.getFieldFromTweetObject(record.value(), "in_reply_to_status_id");
                if(parentTweetId.isEmpty() || parentTweetId.equals("null"))  continue;


                String tweetId = kafkaTweetConsumer.getFieldFromTweetObject(record.value(),"id");
                String created_at = kafkaTweetConsumer.getFieldFromTweetObject(record.value(), "created_at");
                String userObj = kafkaTweetConsumer.getFieldFromTweetObject(record.value(),"user");
                String user = kafkaTweetConsumer.getFieldFromTweetObject(userObj,"name");
                String tweet = kafkaTweetConsumer.getFieldFromTweetObject(record.value(),"text");


                if(redisClient.isInteresting(parentTweetId)) {
                    logger.info(parentTweetId + "__" + kafkaTweetConsumer.getFieldFromTweetObject(record.value(), "id"));
                    ElasticSearchClient._update(parentTweetId,user,tweet,created_at);
                }
            }
        }
    }

}
