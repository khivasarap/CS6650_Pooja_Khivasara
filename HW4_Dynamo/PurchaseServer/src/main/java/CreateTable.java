
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

import com.amazonaws.services.dynamodbv2.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// This class is just to play with DynamoDB; not used as part of the main project

public class CreateTable {

  //            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
  static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
  static DynamoDB dynamoDB = new DynamoDB(client);
  static String tableName = "Purchase2";

  public static void main(String[] args) throws Exception {
    createTable();
//    PurchaseItems item1 = new PurchaseItems("71602",1);
//    List<PurchaseItems> list = new ArrayList<>();
//    list.add(item1);
//    Purchase purchase = new Purchase(1,1,"2010-10-01",new PurchaseList(list));
//    insertTable(purchase);
  }
public static void insertTable(Purchase purchase) throws JsonProcessingException {
  Table table = dynamoDB.getTable(tableName);
  final String uuid = UUID.randomUUID().toString();
  Item item = new Item()
      .withPrimaryKey("Id",uuid)
      .withNumber("storeId",purchase.getStoreId())
      .withNumber("customerId",purchase.getCustomerId())
      .withString("date", purchase.getDate())
      .withJSON("body", new ObjectMapper().writeValueAsString(purchase.getItems().getItems()));
  PutItemOutcome putItemOutcome = table.putItem(item);
  System.out.println(putItemOutcome.getPutItemResult());
}
  public static void createTable() {
    try {
      List<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
      attributeDefinitions
          .add(new AttributeDefinition().withAttributeName("Id").withAttributeType(
              ScalarAttributeType.S));

      List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
      keySchema.add(
          new KeySchemaElement().withAttributeName("Id").withKeyType(KeyType.HASH)); // Partition key
//withBillingMode(BillingMode.PAY_PER_REQUEST)
      CreateTableRequest request = new CreateTableRequest().withTableName(tableName)
          .withKeySchema(keySchema).withAttributeDefinitions(attributeDefinitions).
              withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(5L).withWriteCapacityUnits(5L));

      System.out.println("Issuing CreateTable request for " + tableName);
      Table table = dynamoDB.createTable(request);

      System.out.println("Waiting for " + tableName + " to be created...this may take a while...");
      table.waitForActive();
    } catch (Exception e) {
      System.err.println("CreateTable request failed for " + tableName);
      System.err.println(e.getMessage());
    }

  }


}
