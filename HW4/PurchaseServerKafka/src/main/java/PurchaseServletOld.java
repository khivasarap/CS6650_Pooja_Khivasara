import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.SerializationUtils;
import purchaseMicroservice.Purchase;
import purchaseMicroservice.PurchaseList;

public class PurchaseServletOld extends javax.servlet.http.HttpServlet {

  static Logger log = Logger.getLogger(String.valueOf(PurchaseServletOld.class));
  private final static String QUEUE_NAME = "rabbitMQ"; //Broker instance
  private final static String QUEUE_NAME_PURCHASE = "purchaseMQ"; //StoreMicroservice

  //ChannelPool cp = new ChannelPool();
//  Channel channel;
//  Channel purchaseChannel;
  Connection conn;
  Connection purchaseConn;
  Channel channel;
  Channel purchaseChannel;
  Gson gson = new Gson();

  @Override
  public void init() throws ServletException {
    super.init();
    ExecutorService executor = Executors.newFixedThreadPool(200);
    ConnectionFactory factory = new ConnectionFactory();

//    factory.setHost("18.207.253.144");
//    factory.setUsername("username");
//    factory.setPassword("password");
    factory.setHost("localhost");

    ConnectionFactory purchaseFactory = new ConnectionFactory();
//    purchaseFactory.setHost("54.90.3.109");
//    purchaseFactory.setUsername("username");
//    purchaseFactory.setPassword("password");
    purchaseFactory.setHost("localhost");

    try {
      conn = factory.newConnection(executor);
      purchaseConn = purchaseFactory.newConnection();

      channel = conn.createChannel();
      channel.exchangeDeclare(QUEUE_NAME, "fanout");
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      channel.queueBind(QUEUE_NAME, QUEUE_NAME, "");

      purchaseChannel = purchaseConn.createChannel();
      purchaseChannel.exchangeDeclare(QUEUE_NAME_PURCHASE, "fanout");
      purchaseChannel.queueDeclare(QUEUE_NAME_PURCHASE, true, false, false, null);
      purchaseChannel.queueBind(QUEUE_NAME_PURCHASE, QUEUE_NAME_PURCHASE, "");
    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
    }
  }

  protected void doPost(javax.servlet.http.HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {

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

        //channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, purchaseData);
        //purchaseChannel.basicPublish("", QUEUE_NAME_PURCHASE, MessageProperties.PERSISTENT_TEXT_PLAIN, purchaseData);
        channel.basicPublish("", QUEUE_NAME, null, purchaseData);
        purchaseChannel.basicPublish("", QUEUE_NAME_PURCHASE, null, purchaseData);

        response.setStatus(HttpServletResponse.SC_CREATED);
        //channel.close();
        // purchaseChannel.close();
      }
    } catch (Exception e) {
      log.log(Level.SEVERE, "Failed in Purchase Servlet" + e.getMessage());
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
  protected void doGet(javax.servlet.http.HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {
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
   try {
//      if(channel!=null)
//        channel.close();
//      if(purchaseChannel!=null)
//      purchaseChannel.close();
conn.close();
     purchaseConn.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
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
