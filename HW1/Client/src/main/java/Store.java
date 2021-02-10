import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.PurchaseApi;
import io.swagger.client.model.Purchase;
import io.swagger.client.model.PurchaseItems;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Store implements Runnable {
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
  private static int OPEN_CENTRAL_PHASE_TIME = 3;
  private static int OPEN_WEST_PHASE_TIME = 5;
  private static int STORE_OPERATION_HOURS = 9;

  synchronized public void incRequestCount() {
    requestCount++;
    if (requestCount >= this.numberOfPurchases * OPEN_CENTRAL_PHASE_TIME) {
      //Once this is true, exits the while in main and starts Phase 2
      beginPhase2.compareAndSet(false, true);
    }
    if (requestCount >= this.numberOfPurchases * OPEN_WEST_PHASE_TIME) {
      //Once this is true, exits the while in main and starts Phase 3
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
      AtomicBoolean beginPhase3) {
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
    this.url = "http://"+this.ipAndPort+":8080/Server_war/";
    //For local machine
    //this.url = "http://"+this.ipAndPort+":8080/Server_war_exploded/";

  }

  @Override
  public void run() {
    PurchaseApi purchaseApiInstance = new PurchaseApi();
    purchaseApiInstance.getApiClient().setBasePath(url);
    Integer numPurchases = this.numberOfPurchases;
    for (int i = 0; i < numPurchases * STORE_OPERATION_HOURS; i++) {
      ApiResponse response = null;
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
        response = purchaseApiInstance
            .newPurchaseWithHttpInfo(body, this.storeId, custID, this.date);
        incRequestCount();
        successRequestCount.getAndAdd(1);
      } catch (ApiException e) {
        //System.err.println(e.getMessage());
        //e.printStackTrace();
        log.log(Level.WARNING, e.getMessage());
        failRequestCount.getAndAdd(1);
      } catch (Exception se) {
        //System.err.println(se);
        log.log(Level.WARNING, se.getMessage());
        failRequestCount.getAndAdd(1);
      }
    }
    completed.countDown();
  }
}
