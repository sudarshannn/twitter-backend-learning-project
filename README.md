# twitter-backend-learning-project

# Twitter Backend Project

This is a learning project, in which I have used all technologies I learned throughout
the learning period. And at the end I have made 2 APIs to show workings of the project.

## Description
In this project we will fetch tweets from Twitter and will determine if the interesting ones.
```ruby
Interesting Tweet -> If tweet contains certains workds like putin, virat, modi then it will
be considered as interesting.
```
then we will store those tweets with their replies in Elasticsearch.

## Functionalities 
- API 1
API that gives the most recent interesting tweets.

- API 2
API that gives conversation for any interesting tweet.

- API 3
API that gives the most recent interesting tweets but those tweets will not contains certain words.


## My added feature 
If a user doesn’t want to see tweets of certain keywords, then he can filter those words in API1
## Getting Started

- Tools and Technologies 
intellij idea, MongoDB Compass. Redis, MongoDB, elasticsearch, Kafka installed in local.
Spring Boot.

- Executing program
to run kafka server:

```ruby
kafka-server-start config/server.properties
```

to run kafka zookeeper
```ruby
 zookeeper-server-start config/zookeeper.properties

```

to run Redis server
```ruby
 ./redis-server
```

to run elasticsearch
```ruby
 bin/elasticsearch
```



to run kibana
```ruby
bin/kibana       
```


to run MongoDB on localhost
```ruby
brew services start mongodb-community@5.0
```
## Roadmap

- Create Twitter Devloper Account
Follow the below link to create devloper Account. Then they will provide you some tokens 
which will be used to fetch the tweets.
 https://developer.twitter.com/en/docs/twitter-api/tweets/volume-streams/introduction
 

 - Fetch Tweets Using Hosebird Client(hbc)
hbc is a Java HTTP client for consuming Twitter's standard Streaming API. we will using this API to fetch tweets from twitter
 https://github.com/twitter/hbc/blob/master/hbc-example/src/main/java/com/twitter/hbc/example/FilterStreamExample.java


- Stream fetched tweets using Kafka Producer.
Create Kafka Consumer which will fetch tweets using hbc client and stream that tweets into kafka topic


- Create Regex queries for interesting tweets.
Create mongo Collection in which each document contains Regex Query of interesting word.
for example, if we want to find a tweet which contains "virat" word in it. the Regex 
query will be : 

```ruby
_id : 6218c57872e830cbf64a0a81
regex : "(.*)virat(.*)"
```


- Create Kafka Consumer
consumer will fetch the tweets from kafka topic and match that tweet with Mongo Regex queries
and will determine if tweet is interesting or not.

- Create Redis Client to store interesting Tweet Ids
if the tweet is interesting then store its tweetId in Redis.

- Create another Kafka consumer to check is parent tweets of the reply tweet is interesting
or not using redis.


- Create Index on Elasticsearch
i have created index "tweet" with mapping shown below :
```ruby
 PUT tweets
{
  "mappings": {
    "properties": {
      "user" : {
        "type": "text"
      },
      "tweet" : {
        "type": "text"
      },
      "created_at" : {
        "type":   "date",
        "format": "yyyy-MM-dd HH:mm:ss"
      },
      
      "replies" : {
        "type": "nested",
        "properties": {
          "user" : {
            "type" : "text"
          },
          "tweet" : {
            "type" : "text"
          },
          "created_at" : {
            "type" : "date",
            "format" : "yyyy-MM-dd HH:mm:ss"
          }
        }
      }
    }
  }
}
```


- Save interesting tweets and their replies in Elasticsearch

## Demo

Index.html file 

file:///Users/sudarshan05/Documents/twitter-api-project/index.html
