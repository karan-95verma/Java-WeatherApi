package com.company.repository;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import com.mongodb.client.MongoDatabase;

public class WeatherRepo {

    public MongoDatabase configDatabaseConnect(){
        MongoDatabase database = null;
        try {
            MongoClientURI uri = new MongoClientURI(
                    "mongodb+srv://mydb_user_1:champ1234@cluster0-l9wmy.mongodb.net/test?retryWrites=true&w=majority");

            MongoClient mongoClient = new MongoClient(uri);
            database = mongoClient.getDatabase("test");
        }catch(Exception e){
            System.out.println("Cannot connect to db = "+e);
        }
        return database;
    }

}
