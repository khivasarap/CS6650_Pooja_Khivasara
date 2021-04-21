import com.google.gson.Gson;
import java.io.IOException;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import purchaseMicroservice.Purchase;
import purchaseMicroservice.PurchaseList;

public class PurchaseServlet extends javax.servlet.http.HttpServlet {

  static Logger log = Logger.getLogger(String.valueOf(PurchaseServlet.class));
  private final static String QUEUE_NAME = "rabbitMQ"; //Broker instance
  private final static String QUEUE_NAME_PURCHASE = "purchaseMQ"; //StoreMicroservice

  Gson gson = new Gson();
  Properties config = new Properties();
  KafkaProducer<String, byte[]> producer;
  @Override
  public void init() throws ServletException {
    super.init();
    ExecutorService executor = Executors.newFixedThreadPool(200);
    initializeKafkaProducer();
  }

  private void initializeKafkaProducer() {
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("acks", "all");
    props.put("retries", 0);
    props.put("batch.size", 16384);
    props.put("linger.ms", 1);
    props.put("buffer.memory", 33554432);
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
    producer = new KafkaProducer<>(props);
  }

  protected void doPost(javax.servlet.http.HttpServletRequest request,
      javax.servlet.http.HttpServletResponse response)
      throws javax.servlet.ServletException, IOException {
    response.setContentType("text/plain");
    String urlPath = request.getPathInfo();

    // check if URL exists
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("missing parameters");
      return;
    }
    try {
      String[] urlParts = urlPath.split("/");
      if (!isPurchaseURLValid(urlParts)) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().write("Invalid URL");
        return;
      } else {
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = request.getReader().readLine()) != null) {
          sb.append(s);
        }
        PurchaseList purchaseList = (PurchaseList) gson.fromJson(sb.toString(), PurchaseList.class);
        Purchase purchase = new Purchase(Integer.parseInt(urlParts[1]),
            Integer.parseInt(urlParts[3]), urlParts[5], purchaseList);
        byte[] purchaseData = SerializationUtils.serialize(purchase);
        ProducerRecord<String,byte[]> record = new ProducerRecord<String,byte[]>("purchaseQueue","key" , purchaseData);
        producer.send(record);
//        , new Callback() {
//          public void onCompletion(RecordMetadata metadata, Exception e) {
//            if (e != null)
//              log.log(Level.SEVERE,"Send failed for record "+ record);
//          }
//        });
        response.setStatus(HttpServletResponse.SC_CREATED);
      }
    } catch (Exception e) {
      e.printStackTrace();
      log.log(Level.SEVERE, "Failed in Purchase Servlet" + e.getMessage());
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
  protected void doGet(javax.servlet.http.HttpServletRequest request,
      javax.servlet.http.HttpServletResponse response)
      throws javax.servlet.ServletException, IOException {
    response.setContentType("text/plain");
    String urlPath = request.getPathInfo();

    // check if URL exists
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("missing parameters");
      return;
    }

    String[] urlParts = urlPath.split("/");
    if (!isPurchaseURLValid(urlParts)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("Invalid URL");
    } else {
      response.setStatus(HttpServletResponse.SC_OK);
      response.getWriter().write("It works from server!");
    }
  }

  @Override
  public void destroy() {
    super.destroy();
  }

  private boolean isPurchaseURLValid(String[] urlParts) {
    //[, 28, customer, 28578, date, 20210101]
    //[, 32, customer, 32066, date, 20210101]
    if (urlParts == null || urlParts.length == 0 || urlParts.length > 6 || urlParts.length < 6) {
      return false;
    }
    if (!urlParts[1].isEmpty() && !urlParts[3].isEmpty() && !urlParts[5].isEmpty()) {
      try {
        Integer.parseInt(urlParts[1]);
        Integer.parseInt(urlParts[3]);
        if (urlParts[5].length() < 8) {
          return false;
        }
      } catch (NumberFormatException e) {
        return false;
      }
    }
    if (!urlParts[2].isEmpty() && !urlParts[4].isEmpty()) {
      if (!"customer".equalsIgnoreCase(urlParts[2])) {
        return false;
      }
      if (!"date".equalsIgnoreCase(urlParts[4])) {
        return false;
      }
    }
    return true;
  }
}
