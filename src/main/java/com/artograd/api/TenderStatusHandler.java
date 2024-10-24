package com.artograd.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.artograd.api.constants.CollectionNames;
import com.artograd.api.model.enums.TenderStatus;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.bson.Document;

public class TenderStatusHandler implements RequestHandler<Object, String> {

  String connectionString = System.getenv("ARTOGRAD_MONGO_DB_CONNECTTION");

  @Override
  public String handleRequest(Object input, Context context) {
    try (MongoClient mongoClient = MongoClients.create(connectionString)) {
      String databaseName = connectionString.split("/")[3];
      MongoDatabase database = mongoClient.getDatabase(databaseName);
      MongoCollection<Document> collection = database.getCollection(CollectionNames.TENDER);

      LocalDateTime startOfDay =
          LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
      Date today = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());

      Document filter =
          new Document("status", TenderStatus.PUBLISHED.toString())
              .append("submissionStart", today);
      Document update =
          new Document("$set", new Document("status", TenderStatus.IDEATION.toString()));
      collection.updateMany(filter, update);

    } catch (Exception e) {
      // TODO: Add proper logging
      e.printStackTrace();
      return "Error updating tenders";
    }

    return "Tenders updated successfully";
  }
}
