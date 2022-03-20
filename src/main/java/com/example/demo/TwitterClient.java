package com.example.demo;


import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.*;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


// hbc client
public class TwitterClient {

    private final String consumerKey = "";
    private final String consumerSecret = "";
    private final String token = "";
    private final String secret = "";

    private Client client;
    private BlockingQueue<String> msgQueue ;
    private Hosts host ;
    private StatusesFilterEndpoint endPoint;
    private List<String> defaultTerms = Lists.newArrayList("putin", "virat" , "rohit" , "modi","bjp","rss","world cup","pakistan","america","asia");
    private Authentication authenticator = new OAuth1(consumerKey, consumerSecret, token, secret);

    public TwitterClient() {
        msgQueue = new LinkedBlockingQueue<String>(1000);
        host = new HttpHosts(Constants.STREAM_HOST);
        endPoint = new StatusesFilterEndpoint();
        endPoint.trackTerms(defaultTerms);
        buildClient();
    }

    public void buildClient() {
        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")                              // optional: mainly for the logs
                .hosts(host)
                .authentication(authenticator)
                .endpoint(endPoint)
                .processor(new StringDelimitedProcessor(msgQueue));
        client = builder.build();
    }
    public BlockingQueue<String> getMsgQueue() {
        return this.msgQueue;
    }

    public List<String> getDefaultTerms() {
        return this.defaultTerms;
    }

    public Client getClient() {
        return this.client;
    }

    public void Connect() {
        this.client.connect();
    }

    public void Stop() {
        this.client.stop();
    }

    public String getConsumerKey() {
        return consumerKey;
    }

}
