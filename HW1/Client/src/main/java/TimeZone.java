import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeZone {
  private int numThreads;
  private InputParameter inputParameter;
  private AtomicInteger successRequestCount;
  private AtomicInteger failRequestCount;
  private AtomicBoolean beginCentralPhase;
  private AtomicBoolean beginWestPhase;

  public TimeZone(int numThreads, InputParameter inputParameter,
      AtomicInteger successRequestCount, AtomicInteger failRequestCount, AtomicBoolean beginCentralPhase, AtomicBoolean beginWestPhase) {
    this.numThreads = numThreads;
    this.inputParameter = inputParameter;
    this.successRequestCount = successRequestCount;
    this.failRequestCount = failRequestCount;
    this.beginCentralPhase = beginCentralPhase;
    this.beginWestPhase = beginWestPhase;
  }

  public void beginEastPhase() throws InterruptedException {
    CountDownLatch completed = new CountDownLatch(this.numThreads);
    ExecutorService executor = Executors.newFixedThreadPool(this.numThreads);
    int numberOfPurchases = inputParameter.getNumPurchases();
    int numberOfCustomersPerStore = inputParameter.getNumberOfCustomers();
    int numberOfItemsPerPurchase = inputParameter.getNumberOfItemsPerPurchase();
    int maxItemID = inputParameter.getMaxItemId();
    String ipAndPort = inputParameter.getIpAndPort();
    String date = inputParameter.getDate();
    for (int i = 0; i < this.numThreads; i++) {
      Runnable thread = new Store(i,numberOfPurchases, numberOfCustomersPerStore,
          maxItemID, numberOfItemsPerPurchase, ipAndPort, date, successRequestCount, failRequestCount, completed,
          beginCentralPhase, beginWestPhase);
      executor.execute(thread);
    }
    //completed.await(); //Blocks the main thread
    executor.shutdown();
  }
  public void beginCentralPhase() throws InterruptedException {
    CountDownLatch completed = new CountDownLatch(this.numThreads);
    ExecutorService executor = Executors.newFixedThreadPool(this.numThreads);
    int numberOfPurchases = inputParameter.getNumPurchases();
    int numberOfCustomersPerStore = inputParameter.getNumberOfCustomers();
    int numberOfItemsPerPurchase = inputParameter.getNumberOfItemsPerPurchase();
    int maxItemID = inputParameter.getMaxItemId();
    String ipAndPort = inputParameter.getIpAndPort();
    String date = inputParameter.getDate();
    for (int i = 0; i < this.numThreads; i++) {
      Runnable thread = new Store(i,numberOfPurchases, numberOfCustomersPerStore,
          maxItemID, numberOfItemsPerPurchase, ipAndPort, date, successRequestCount, failRequestCount, completed,
          beginCentralPhase, beginWestPhase);
      executor.execute(thread);
    }
    //completed.await(); //Blocks the main thread
    executor.shutdown();
  }
  public void beginWestPhase() throws InterruptedException {
    CountDownLatch completed = new CountDownLatch(this.numThreads);
    ExecutorService executor = Executors.newFixedThreadPool(this.numThreads);
    int numberOfPurchases = inputParameter.getNumPurchases();
    int numberOfCustomersPerStore = inputParameter.getNumberOfCustomers();
    int numberOfItemsPerPurchase = inputParameter.getNumberOfItemsPerPurchase();
    int maxItemID = inputParameter.getMaxItemId();
    String ipAndPort = inputParameter.getIpAndPort();
    String date = inputParameter.getDate();
    for (int i = 0; i < this.numThreads; i++) {
      Runnable thread = new Store(i,numberOfPurchases, numberOfCustomersPerStore,
          maxItemID, numberOfItemsPerPurchase, ipAndPort, date, successRequestCount, failRequestCount, completed,
          beginCentralPhase, beginWestPhase);
      executor.execute(thread);
    }
    completed.await(); //Blocks the main thread
    executor.shutdown();
  }
}