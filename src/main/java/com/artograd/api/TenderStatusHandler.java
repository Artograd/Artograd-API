package com.artograd.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.Date;
import org.bson.Document;

public class TenderStatusHandler implements RequestHandler<Object, String> {

  String connectionString = System.getenv("ARTOGRAD_MONGO_DB_CONNECTTION");

  String collectionName = "tender";

  @Override
  public String handleRequest(Object input, Context context) {
    try (MongoClient mongoClient = MongoClients.create(connectionString)) {
      String databaseName = connectionString.split("/")[3];
      MongoDatabase database = mongoClient.getDatabase(databaseName);
      MongoCollection<Document> collection = database.getCollection(collectionName);
      Date today = new Date();
      Document filter = new Document("status", "PUBLISHED").append("submissionStart", today);
      Document update = new Document("$set", new Document("status", "IDEATION"));
      collection.updateMany(filter, update);

      System.out.println("Documents updated successfully");
    } catch (Exception e) {
      e.printStackTrace();
      return "Error updating tenders";
    }

    return "Updated tenders to ACTIVE";
  }
}
