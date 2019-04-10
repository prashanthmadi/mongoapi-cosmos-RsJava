package com.ms.consoleapp;

import com.mongodb.reactivestreams.client.*;
import org.bson.*;

import static java.util.Arrays.asList;

public class MongoApiTest {

    public static void main(String[] args) throws Throwable {


        String connectionString = "mongodb://mongoapipr:Sopcfk46JcrNIiTfd0Vj6YdbdcA5L8Fq6RzJpu24VJxbE1nfyure2ysjOv23sRtukRf7D5FeXZaI4Gw0AvLkqQ==@mongoapipr.documents.azure.com:10255/?ssl=true&replicaSet=globaldb";
        String databaseName = "testdb";
        String collectionName = "testcol";
        String partitionKey = "Country";

        // create Connection
        MongoClient mongoClient = MongoClients.create(connectionString);

        // getting a list of databases and printing them using PrintStatus Subscriber
        mongoClient.listDatabaseNames().subscribe(new PrintStatus<String>("Database Names: %s"));

        // get a handle of the database
        MongoDatabase database = mongoClient.getDatabase(databaseName);


        ObservableSubscriber<Document> obsSubscriber = new ObservableSubscriber<Document>();

        // create new partitioned collection
        int rus = 800;
        Document col1 = new Document("customAction", "CreateCollection")
                .append("collection", collectionName).append("offerThroughput", rus)
                .append("shardKey",partitionKey);
        database.runCommand(col1).subscribe(obsSubscriber);
        // ignoring below await will  create fixed collection :(
        obsSubscriber.await();

        // updated collection RU's
        int updatedRus = 1200;
        obsSubscriber = new ObservableSubscriber<Document>();
        Document cmd2 = new Document("customAction", "UpdateCollection").
                append("collection", collectionName).append("offerThroughput", updatedRus);
        database.runCommand(cmd2).subscribe(obsSubscriber);
        obsSubscriber.await();


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
//        subscriber = new ObservableSubscriber<Success>();
//        collection.drop().subscribe(subscriber);
//        subscriber.await();


        mongoClient.close();

    }
}
