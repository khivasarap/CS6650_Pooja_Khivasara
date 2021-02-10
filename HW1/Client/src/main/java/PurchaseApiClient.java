import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PurchaseApiClient {

  public static void main(String[] args) throws InterruptedException {
    Logger log = Logger.getLogger(String.valueOf(PurchaseApiClient.class));
    log.log(Level.INFO,"Begin the client program");
    try {
      AtomicInteger successRequestCount = new AtomicInteger(0);
      AtomicInteger failRequestCount = new AtomicInteger(0);
      AtomicBoolean beginCentralPhase = new AtomicBoolean(false);
      AtomicBoolean beginWestPhase = new AtomicBoolean(false);

      long startTime = System.currentTimeMillis();
      InputParameter inputParameter = InputProcessing.processInput();
      int maxStores = inputParameter.getMaxStores();

      // Begin Phase 1 : East Phase
      TimeZone eastPhase = new TimeZone(maxStores / 4, inputParameter, successRequestCount,
          failRequestCount, beginCentralPhase, beginWestPhase);
      eastPhase.beginEastPhase();
      while (!beginCentralPhase.get()) {
        //wait till *3 requests
      }
      // Begin Phase 2 : Central Phase
      TimeZone centralPhase = new TimeZone(maxStores / 4, inputParameter, successRequestCount,
          failRequestCount, beginCentralPhase, beginWestPhase);
      centralPhase.beginCentralPhase();
      while (!beginWestPhase.get()) {
        //wait till *5 requests
      }
      // Begin Phase 3 : West Phase
      TimeZone westPhase = new TimeZone(maxStores / 2, inputParameter, successRequestCount,
          failRequestCount, beginCentralPhase, beginWestPhase);
      westPhase.beginWestPhase();
      long endTime = System.currentTimeMillis();
      // Display Information
      displayMetrics(successRequestCount, failRequestCount, startTime, endTime);
    } catch (IllegalArgumentException e) {
      log.log(Level.WARNING, e.getMessage());
    }
  }

  private static void displayMetrics(AtomicInteger successRequestCount,
      AtomicInteger failRequestCount, long startTime, long endTime) {
    System.out.println("Total number of successful requests sent: " + successRequestCount.get());
    System.out.println("Total number of unsuccessful requests sent: " + failRequestCount.get());
    long totalRequest = successRequestCount.get() + failRequestCount.get();
    long wallTime = (endTime - startTime) / 1000;
    System.out.println("Total runtime for phases to complete (Wall Time) :" + wallTime  + " sec");
    System.out.println("Throughput: " + (totalRequest / wallTime));
  }
}