package com.example.demo;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoClientTemplate {
    private static MongoClientTemplate mongoClientTemplate;
    private static String uri;
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;

    private MongoClientTemplate() {
        uri = "mongodb://localhost:27017/";
        mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase("project_week7");
        collection = database.getCollection("Regex_Filters");
    }

    public static MongoClientTemplate getInstance() {
        if(mongoClientTemplate == null)
            mongoClientTemplate = new MongoClientTemplate();

        return mongoClientTemplate;
    }

    public static MongoCollection<Document> getCollection() {

        return collection;
    }

    public static MongoClient getMongoClient() {
        return mongoClient;
    }

    public static MongoDatabase getDatabase() {
        return database;
    }

    public static void Close() {
        mongoClient.close();
    }
}
