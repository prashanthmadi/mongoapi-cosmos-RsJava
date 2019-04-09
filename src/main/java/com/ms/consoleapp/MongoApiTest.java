package com.ms.consoleapp;

import com.mongodb.reactivestreams.client.*;
import org.bson.Document;

import static java.util.Arrays.asList;

public class MongoApiTest {

    public static void main(String[] args) throws Throwable {


        String connectionString = "mongodb://mongoapipr:mYfGaSXkDTlnxwv6YKyXVLP5LVdrGVysTCtytVddgKFcSJD14w83QGSlFvC1xrJQX9dqBmXl2sHyypg9TRx0IA==@mongoapipr.documents.azure.com:10255/?ssl=true&replicaSet=globaldb";
        String databaseName = "testdb";
        String collectionName = "testcol";

        MongoClient mongoClient = MongoClients.create(connectionString);

        // getting a list of databases and printing them using PrintStatus Subscriber
        mongoClient.listDatabaseNames().subscribe(new PrintStatus<String>("Database Names: %s"));

        // get a handle of the database
        MongoDatabase database = mongoClient.getDatabase(databaseName);

        // getting a list of collections and printing them using PrintStatus Subscriber
        database.listCollectionNames().subscribe(new PrintStatus<String>("Collection Names: %s"));

        // get a handle to the collection
        MongoCollection<Document> collection = database.getCollection(collectionName);

        ObservableSubscriber subscriber;

        try {
            subscriber = new ObservableSubscriber();
            collection.insertMany(asList(new Document("id", "AB").append("Country", "RSA"),
                    new Document("id", "Sachin").append("Country", "India"),
                    new Document("id", "Pointing").append("Country", "Australia"))).subscribe(subscriber);
            subscriber.await();

        } catch (Exception e) {
            e.printStackTrace();
        }


        // drop collection
        subscriber = new ObservableSubscriber<Success>();
        collection.drop().subscribe(subscriber);
        subscriber.await();


        mongoClient.close();

    }
}
