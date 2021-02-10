import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PurchaseApiClient {

  public static void main(String[] args) throws InterruptedException {
    Logger log = Logger.getLogger(String.valueOf(PurchaseApiClient.class));
    try {
      log.log(Level.INFO,"Begin the client program");
      AtomicInteger successRequestCount = new AtomicInteger(0);
      AtomicInteger failRequestCount = new AtomicInteger(0);
      AtomicBoolean beginCentralPhase = new AtomicBoolean(false);
      AtomicBoolean beginWestPhase = new AtomicBoolean(false);
      AtomicLong totalResponse = new AtomicLong(0);
      List<Long> responseTimes = Collections.synchronizedList(new ArrayList<Long>());
      long startTime = System.currentTimeMillis();
      InputParameter inputParameter = InputProcessing.processInput();
      //private static String url = "http://localhost:8080/Server_war_exploded/purchase/123";
      int maxStores = inputParameter.getMaxStores();

      // Begin Phase 1 : East Phase
      TimeZone eastPhase = new TimeZone(maxStores / 4, inputParameter, successRequestCount,
          failRequestCount, beginCentralPhase, beginWestPhase, responseTimes, totalResponse);
      eastPhase.beginEastPhase();
      while (!beginCentralPhase.get()) {
        //wait till *3 requests
      }
      // Begin Phase 2 : Central Phase
      TimeZone centralPhase = new TimeZone(maxStores / 4, inputParameter, successRequestCount,
          failRequestCount, beginCentralPhase, beginWestPhase, responseTimes, totalResponse);
      centralPhase.beginCentralPhase();
      while (!beginWestPhase.get()) {
        //wait till *5 requests
      }
      // Begin Phase 3 : West Phase
      TimeZone westPhase = new TimeZone(maxStores / 2, inputParameter, successRequestCount,
          failRequestCount, beginCentralPhase, beginWestPhase, responseTimes,totalResponse);
      westPhase.beginWestPhase();
      long endTime = System.currentTimeMillis();
      // Display Information
      displayMetrics(successRequestCount, failRequestCount, totalResponse, responseTimes,
          startTime,
          endTime);
    } catch (IllegalArgumentException e) {
      log.log(Level.WARNING, e.getMessage());
    }
  }

  private static void displayMetrics(AtomicInteger successRequestCount,
      AtomicInteger failRequestCount, AtomicLong totalResponse, List<Long> responseTimes,
      long startTime, long endTime) {
    System.out.println("Total number of successful requests sent: " + successRequestCount.get());
    System.out.println("Total number of unsuccessful requests sent: " + failRequestCount.get());
    long totalRequest = successRequestCount.get() + failRequestCount.get();
    long wallTime = (endTime - startTime) / 1000;
    System.out.println("Total runtime for phases to complete (Wall Time) :" + wallTime  + " sec");
    System.out.println("Throughput: " + (totalRequest / wallTime));
    System.out.println("Mean: "+ (double)totalResponse.get()/(double)(successRequestCount.get() + failRequestCount.get()));
    Collections.sort(responseTimes);
    System.out.println("Median: "+ findMedian(responseTimes));
    System.out.println("P99: "+ responseTimes.get((int)Math.ceil(0.99*responseTimes.size())));
    System.out.println("Maximum response Time for POSTs: "+ responseTimes.get(responseTimes.size()-1));
  }

  private static double  findMedian(List<Long> responseTimes) {
    if(responseTimes.size()%2==0) {
      Long mid1 = responseTimes.get(responseTimes.size()/2);
      Long mid2 = responseTimes.get((responseTimes.size()/2)+1);
      return (double)(mid1+mid2)*0.5;
    }
    else
      return (double) responseTimes.get(responseTimes.size()/2);
  }
}