import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DAO {
//Logger log;
//AmazonDynamoDB client;
//DynamoDB dynamoDB;
//String tableName;
//Table table;
  static Logger log = Logger.getLogger(String.valueOf(DAO.class));
  //Ensure to add IAM role to EC2 instance when running Jar on ec2


  public DAO() {
//    try {
//      log = Logger.getLogger(String.valueOf(DAO.class));
//      client = AmazonDynamoDBClientBuilder.standard()
//          .withRegion(Regions.US_EAST_1).build();
//      dynamoDB = new DynamoDB(client);
//      tableName = "Purchase";
//      table = dynamoDB.getTable(tableName);
//    } catch (Exception e) {
//      e.printStackTrace();
//      System.out.println("Exception in DAO");
//    }
  }

  public static void insertIntoDynamo(Purchase purchase, Table table) {
    try {
//       AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
//      .withRegion(Regions.US_EAST_1).build();
//   DynamoDB dynamoDB = new DynamoDB(client);
//   String tableName = "Purchase";
//   Table table = dynamoDB.getTable(tableName);
      String uuid = UUID.randomUUID().toString();
//      String uuid = purchase.getStoreId()+"-"+purchase.getCustomerId()+"-"+ System.currentTimeMillis();
      Item item = new Item()
          .withPrimaryKey("Id", uuid)
          .withNumber("storeId", purchase.getStoreId())
          .withNumber("customerId", purchase.getCustomerId())
          .withString("date", purchase.getDate())
          .withJSON("body", new ObjectMapper().writeValueAsString(purchase.getItems().getItems()));
      PutItemOutcome putItemOutcome = table.putItem(item);
      //client.shutdown();
    } catch (Exception e) {
      log.log(Level.SEVERE, "DAO insertDynamo" + e.getMessage());
      e.printStackTrace();
    }
  }
}
