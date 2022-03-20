package com.example.demo;


import org.apache.http.HttpHost;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.update.UpdateRequest;
import org.opensearch.action.update.UpdateResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.script.Script;
import org.opensearch.script.ScriptType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class ElasticSearchClient {
    private static RestClientBuilder builder = RestClient.builder(new HttpHost("localhost",9200));
    private static RestHighLevelClient client = new RestHighLevelClient(builder);
    private static String indexName = "tweets";
    private static Logger logger = LoggerFactory.getLogger(ElasticSearchClient.class.getName());


    public static void PUT(String tweetId ,String user,String tweet, String created_at) {
        created_at = getEsFormDate(created_at);
        IndexRequest indexRequest = new IndexRequest(indexName);
        indexRequest.id(tweetId);
        HashMap<String,Object> content = new HashMap<>();
        content.put("user",user);
        content.put("tweet",tweet);
        content.put("created_at",created_at);
        content.put("replies" ,Collections.EMPTY_LIST);

        indexRequest.source(content);

        try {
            synchronized (client) {
                IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
                logger.info(response.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void _update(String tweetId,String user,String tweet, String created_at) {
        created_at = getEsFormDate(created_at);
        UpdateRequest request = new UpdateRequest(indexName,tweetId);
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("user",user);
        parameters.put("tweet",tweet);
        parameters.put("created_at",created_at);

        Script inline = new Script(ScriptType.INLINE, "painless",
                "ctx._source.replies.add(params)", parameters);

        request.script(inline);

        try {
            synchronized (client) {
                UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
                logger.info(updateResponse.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static RestClientBuilder getBuilder() {
        return builder;
    }

    public static String getEsFormDate(String old) {
        String[] arr = old.split(" ");
        return "2022" + "-" + Months.getMonthNumber(arr[1]) + "-" + arr[2]  + " " + arr[3];
    }
    public static void Close() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void go() {
        System.out.println("YES");
    }

}
