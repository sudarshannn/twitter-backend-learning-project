package com.example.demo;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;


// consumer will take tweets from kafka-topic and will determine if it is interesting or not.
public class KafkaTweetConsumer implements Runnable {
    private KafkaConsumer<String ,String> consumer;
    private String grpId;
    private Logger logger;
    private List<String > regexFilters;
    private static Integer readCount = 0;


    public KafkaTweetConsumer(String grpId) {
        this.grpId = grpId;
        logger = LoggerFactory.getLogger(KafkaTweetConsumer.class.getName());
        consumer = getNewConsumer();
        regexFilters = getRegexFromMongodb();
    }

    public void run() {
        new Thread(new FetchTweet_and_push_to_redis()).start();
        new Thread(new Is_parent_tweet_interesting()).start();
        new Thread(new GetRegexFromMongo()).start();
    }


    public KafkaConsumer<String,String> getConsumer() {
        return consumer;
    }

    public List<String> getRegexFilters() {
        return new ArrayList<>(regexFilters);
    }

    public String getFieldFromTweetObject(String jsonObject , String field) {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(jsonNode == null) return "";
        JsonNode result = jsonNode.get(field);

        if(result == null) return "";
        return result.toString();
    }

    public Boolean isInteresting(String text,List<String> regexFilters) {

        text = text.toLowerCase(Locale.ROOT);
        for(String regex : regexFilters) {
            if(Pattern.matches(regex,text)) return true;
        }

        return false;
    }

    public void incr_ReadCount() {
        synchronized (readCount) {
            readCount++;
        }
    }

    public void decr_Readcount() {
        synchronized (readCount) {
            readCount--;
        }
    }
    public Integer getReadCount() {

        synchronized (readCount) {
            return readCount;
        }

    }

    public class GetRegexFromMongo implements Runnable {
        @Override
        public void run() {
            Runtime.getRuntime().addShutdownHook(new Thread( () -> {
                logger.info("Thread-3 stopped !!!");
                MongoClientTemplate.Close();
            }));

            while(true) {
                synchronized (regexFilters) {
                    regexFilters = getRegexFromMongodb();
                    logger.info("Re-Fetching Regexes from MongoDb");
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<String> getRegexFromMongodb() {

        MongoClientTemplate mongoClientTemplate = MongoClientTemplate.getInstance();
        MongoCollection<Document> collection = MongoClientTemplate.getCollection();

        List<Document> QueryResult = new ArrayList<>();
        List<String> Regexes = new ArrayList<>();

        Bson query = new Document();
        collection.find(query).into(QueryResult);


        for(Document doc :QueryResult) {
            Regexes.add((String) doc.get("regex"));
        }

        return Regexes;
    }


    public KafkaConsumer<String ,String > getNewConsumer() {
        Properties prop = new Properties();
        prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        prop.put(ConsumerConfig.GROUP_ID_CONFIG,grpId);
        prop.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        prop.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,"10000");

        KafkaConsumer<String ,String > consumer = new KafkaConsumer<String, String>(prop);
        consumer.subscribe(Arrays.asList(KafkaProperties.TOPIC));
        return consumer;
    }

}
