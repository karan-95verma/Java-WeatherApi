package com.company.service;

import com.company.repository.WeatherRepo;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;


public class WeatherService {

    private final String APIKEY = "c66c0fe648cee2169ddb39b6aa2fdf08";
    private WeatherRepo weatherRepo;

    public WeatherService() {
        weatherRepo = new WeatherRepo();
    }

    public MongoDatabase connectToDb() {
        return weatherRepo.configDatabaseConnect();
    }

    public void getFiveDaysThreeHourForecast(String cityId, MongoDatabase db) {

        String url = "http://api.openweathermap.org/data/2.5/forecast?id=" + cityId + "&appid=" + APIKEY;
        String response = getResponse(url);
        if (response == null)
            return;
        MongoCollection<Document> documentMongoCollection = createCollection("FiveDaysData", db);

        BasicDBObject query = new BasicDBObject();
        query.put("_id",cityId);

        documentMongoCollection.findOneAndDelete(query);
        Document d1 = new Document("_id", cityId);
        d1.append("data", response);

        documentMongoCollection.insertOne(d1);
    }

    public void getCurrentWeather(String cityId, MongoDatabase db) {
        String url = "http://api.openweathermap.org/data/2.5/weather?id=" + cityId + "&appid=" + APIKEY;
        String response = getResponse(url);
        if (response == null)
            return;
        MongoCollection<Document> documentMongoCollection = createCollection("CurrentWeather", db);

        BasicDBObject query = new BasicDBObject();
        query.put("_id", cityId);

        documentMongoCollection.findOneAndDelete(query);
        Document d1 = new Document("_id", cityId);
        d1.append("data", response);

        documentMongoCollection.insertOne(d1);

    }

    private String getResponse(String url) {
        URL obj = null;
        try {
            obj = new URL(url);
            HttpURLConnection httpConnection = (HttpURLConnection) obj
                    .openConnection();

            httpConnection.setRequestMethod("GET");

            int responseCode = httpConnection.getResponseCode();
            if (responseCode == 200) {

                BufferedReader responseReader = new BufferedReader(new InputStreamReader(
                        httpConnection.getInputStream()));

                String responseLine;
                StringBuffer response = new StringBuffer();

                while ((responseLine = responseReader.readLine()) != null) {
                    response.append(responseLine + "\n");
                }
                responseReader.close();

                return response.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private MongoCollection<Document> createCollection(String collectionName, MongoDatabase db) {
        try {

            db.createCollection(collectionName);
        } catch (MongoCommandException e) {

            db.getCollection(collectionName).drop();
        }

        return db.getCollection(collectionName);
    }

    public void displayFiveDaysData(MongoDatabase db){
        MongoCollection<Document> collection = db.getCollection("FiveDaysData");

        System.out.println("----[ Show All Five Days 3 hour Data  ]----");

        for (Document doc : collection.find()) {
            System.out.println(doc.toJson());
        }

    }

    public void displayCurrentWeather(MongoDatabase db){
        MongoCollection<Document> collection = db.getCollection("CurrentWeather");

        System.out.println("----[ Show All Current Weather  Data  ]----");

        for (Document doc : collection.find()) {
            System.out.println(doc.toJson());
        }

    }

}
