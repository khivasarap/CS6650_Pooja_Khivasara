import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.PurchaseApi;
import io.swagger.client.model.Purchase;
import io.swagger.client.model.PurchaseItems;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class Store implements Runnable {

  private static final String CSV_FILE = "./output.csv";

  static Logger log = Logger.getLogger(String.valueOf(Store.class));
  private int storeId;
  private int numberOfPurchases;
  private int numberOfCustomersPerStore;
  private int maxItemID;
  private int numberOfItemsPerPurchase;
  private String ipAndPort;
  private String url;
  private String date;
  private AtomicInteger successRequestCount;
  private AtomicInteger failRequestCount;
  private AtomicBoolean beginPhase2;
  private AtomicBoolean beginPhase3;
  private CountDownLatch completed;
  ThreadLocalRandom random = ThreadLocalRandom.current();
  private int requestCount = 0;
  private List<Long> responseTimes;
  private AtomicLong totalResponse;
  private static int OPEN_CENTRAL_PHASE_TIME = 3;
  private static int OPEN_WEST_PHASE_TIME = 5;
  private static int STORE_OPERATION_HOURS = 9;

  synchronized public void incRequestCount() {
    requestCount++;
    if (requestCount >= this.numberOfPurchases * OPEN_CENTRAL_PHASE_TIME) {
      beginPhase2.compareAndSet(false, true);
    }
    if (requestCount >= this.numberOfPurchases * OPEN_WEST_PHASE_TIME) {
      beginPhase3.compareAndSet(false, true);
    }
  }

  public int getRequestCount() {
    return this.requestCount;
  }

  public Store(int storeId, int numberOfPurchases, int numberOfCustomersPerStore, int maxItemID,
      int numberOfItemsPerPurchase, String ipAndPort, String date,
      AtomicInteger successRequestCount,
      AtomicInteger failRequestCount, CountDownLatch completed, AtomicBoolean beginPhase2,
      AtomicBoolean beginPhase3, List<Long> responseTimes,
      AtomicLong totalResponse) {
    this.storeId = storeId;
    this.numberOfPurchases = numberOfPurchases;
    this.numberOfCustomersPerStore = numberOfCustomersPerStore;
    this.maxItemID = maxItemID;
    this.numberOfItemsPerPurchase = numberOfItemsPerPurchase;
    this.date = date;
    this.ipAndPort = ipAndPort;
    this.successRequestCount = successRequestCount;
    this.failRequestCount = failRequestCount;
    this.completed = completed;
    this.beginPhase2 = beginPhase2;
    this.beginPhase3 = beginPhase3;
    this.responseTimes = responseTimes;
    this.totalResponse = totalResponse;
    this.url = "http://"+this.ipAndPort+":8080/PurchaseServer_war/";
    //this.url = "http://"+this.ipAndPort+":8082/PurchaseServer_war_exploded/";

  }

  @Override
  public void run() {
    PurchaseApi purchaseApiInstance = new PurchaseApi();
    purchaseApiInstance.getApiClient().setBasePath(this.url);
    //purchaseApiInstance.getApiClient().setConnectTimeout(15000);
    Integer numPurchases = this.numberOfPurchases;
    List<List<Object>> fileStore = new ArrayList<>();
    for (int i = 0; i < numPurchases * STORE_OPERATION_HOURS; i++) {
      ApiResponse response = null;
      long requestStartTime = 0;
      List<Object> fileRow = new ArrayList<>();
      try {
        Integer custID = random
            .nextInt(this.storeId * 1000, (this.storeId * 1000 + this.numberOfCustomersPerStore));
        Integer numberOfItemsPurchased = random
            .nextInt(1, this.numberOfItemsPerPurchase);

        Purchase body = new Purchase(); // Purchase | items purchased
        List<PurchaseItems> purchaseItems = new ArrayList<>();
        int amount = 1;
        for(int count =0;count<numberOfItemsPurchased;count++) {
          PurchaseItems purchaseItem = new PurchaseItems();
          Integer itemId = random.nextInt(1, this.maxItemID);
          purchaseItem.setItemID(String.valueOf(itemId));
          purchaseItem.setNumberOfItems(amount);
          purchaseItems.add(purchaseItem);
        }
        body.setItems(purchaseItems);
        requestStartTime = System.currentTimeMillis();
        response = purchaseApiInstance
            .newPurchaseWithHttpInfo(body, this.storeId, custID, this.date);
        incRequestCount();
        successRequestCount.getAndAdd(1);
      } catch (ApiException e) {
        //System.err.println(e);
        log.log(Level.WARNING, e.getMessage());
        failRequestCount.getAndAdd(1);
      } catch (Exception se) {
        //System.err.println(se);
        log.log(Level.WARNING, se.getMessage());
        failRequestCount.getAndAdd(1);
      } finally {
        if(response!=null) {
          long responseReceivedTime = System.currentTimeMillis();
          long latency = responseReceivedTime - requestStartTime;
          responseTimes.add(latency);
          totalResponse.addAndGet(latency);
          fileRow.add(requestStartTime);
          fileRow.add("POST");
          fileRow.add(latency);
          fileRow.add(response.getStatusCode());
          fileStore.add(fileRow);
        }
      }
    }
    //Write all request to file
    try {
      synchronized(CSV_FILE) {
        writeToCsv(CSV_FILE, fileStore);
      }
    } catch (IOException e) {
      System.err.println(e);
      e.printStackTrace();
    }
    completed.countDown();
  }

  private void writeToCsv(String file,List<List<Object>> storeThread) throws IOException {
    BufferedWriter writer = Files.newBufferedWriter(Paths.get(file), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
    for(List<Object> row:storeThread){
      printer.printRecord(row.get(0),row.get(1), row.get(2), row.get(3));
    }
    printer.flush();
    writer.close();
  }
}
