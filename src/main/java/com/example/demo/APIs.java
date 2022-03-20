package com.example.demo;


import org.apache.http.HttpHost;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
public class APIs {
    private static RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200)));
    private static Logger logger = LoggerFactory.getLogger(APIs.class.getName());
    private static String indexName = "tweets";


    @GetMapping("/GetTweets")
    public List<Map<String,Object>> GetRecentInterestingTweet(
            @RequestParam(name="not", required = false,defaultValue = "$$$$$$$$$$$$$cipher$$$$") String word,
            @RequestParam(name="size", required = false,defaultValue = "10") Integer size) {

        System.out.println(word);
        List<String> notWords = Arrays.asList(word.split(","));


        SearchRequest request = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        for(String notWord : notWords) {
            boolQueryBuilder.mustNot(QueryBuilders.wildcardQuery("tweet","*" + notWord + "*"));
        }
        searchSourceBuilder.query(boolQueryBuilder);

        searchSourceBuilder.size(Integer.parseInt(String.valueOf(size)));
        searchSourceBuilder.sort("created_at", SortOrder.DESC);


        request.source(searchSourceBuilder);

        SearchResponse response = null;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response == null ) new ArrayList<Map<String,String>>();

        SearchHits hits = response.getHits();

        SearchHit[] searchHits = hits.getHits();

        ArrayList<Map<String,Object>> result = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            Map<String,Object> content = hit.getSourceAsMap();
            result.add(content);
        }
        return result;
    }

    @GetMapping("GetTweetsFromId")
    public Map<String, Object> GetConversationFromTweetId(@RequestParam(name="id", required=true) String tweetId) {

        GetRequest request = new GetRequest(indexName);
        request.id(tweetId);

        GetResponse response = null;
        try {
            response = client.get(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response == null) new HashMap<String,Object>();

        Map<String, Object> source = response.getSourceAsMap();
        return source;
    }

}
