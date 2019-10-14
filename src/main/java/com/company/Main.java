package com.company;

import com.company.service.WeatherService;
import com.mongodb.client.MongoDatabase;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main implements Runnable {

    public static void main(String[] args) {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "application.properties";
        String catalogConfigPath = rootPath + "catalog";
        WeatherService weatherService = new WeatherService();
        MongoDatabase db = weatherService.connectToDb();

        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(appConfigPath));
            String cityId = appProps.getProperty("la.city.id");

            Runnable runnable =
                    () -> {
                        System.out.println("Thread 1 running");
                        weatherService.getFiveDaysThreeHourForecast(cityId, db);
                    };

            Thread thread = new Thread(runnable);
            thread.start();

            Runnable runnable2 =
                    () -> {
                        System.out.println("Thread 2 running");
                        weatherService.getCurrentWeather(cityId, db);
                    };

            Thread thread2 = new Thread(runnable2);
            thread2.start();

            Runnable runnable3 =
                    () -> {
                        try {
                            Thread.sleep(3000);
                            System.out.println("Thread 3 running");
                            weatherService.displayFiveDaysData(db);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    };

            Thread thread3 = new Thread(runnable3);
            thread3.start();

            Runnable runnable4 =
                    () -> {
                        try {
                            Thread.sleep(6000);
                            System.out.println("Thread 4 running");
                            weatherService.displayCurrentWeather(db);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    };

            Thread thread4 = new Thread(runnable4);
            thread4.start();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {

    }
}
