package com.example.demo;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;

public class RedisClient {
    private static RedisClient redisClient;
    private static JedisPool jedisPool;
    private String key;
    private Integer total_tweet_id;

    private RedisClient() {
        jedisPool = new JedisPool(new JedisPoolConfig(),HostPort.getRedisHost(),HostPort.getRedisPort());
        key = "tweetIds";
        total_tweet_id = 0;
    }

    public static RedisClient getInstance() {
        if(redisClient == null)
            redisClient = new RedisClient();
        return redisClient;
    }
    public void addTweetId(String tweetId) {
        if(tweetId == null)
            return ;
        try(Jedis jedis = jedisPool.getResource()) {
            if(jedis.sadd(key,tweetId) > 0)
                total_tweet_id++;
        }
    }

    public String getKey() {
        return key;
    }

    public Integer getTotal_tweet_id() {
        return total_tweet_id;
    }

    public void PrintTweetIds() {
        try(Jedis jedis = jedisPool.getResource()) {
            Set<String> Ids = jedis.smembers(key);

            for(String id : Ids) {
                System.out.println(id);
            }
        }
    }
    public static void Close() {
        jedisPool.close();
    }

    Boolean isInteresting(String tweetId) {
        try(Jedis jedis = jedisPool.getResource()) {
            return jedis.sismember(key,tweetId);
        }
    }
}
