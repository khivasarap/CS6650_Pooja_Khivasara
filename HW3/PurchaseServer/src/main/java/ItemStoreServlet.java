import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

public class ItemStoreServlet extends javax.servlet.http.HttpServlet {

  private Connection connection;
  private Channel channel;
  private String requestQueueName = "rpc_queue"; //On StoreMicroservice
  ExecutorService executor = Executors.newFixedThreadPool(100);

  @Override
  public void init() throws ServletException {
    super.init();
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("54.90.3.109");
    factory.setUsername("username");
    factory.setPassword("password");
    //factory.setHost("localhost");

    try {
      connection = factory.newConnection(executor);

    } catch (IOException e) {
      e.printStackTrace();
    } catch (TimeoutException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void destroy() {
    super.destroy();
    try {
      channel.close();
      connection.close();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (TimeoutException e) {
      e.printStackTrace();
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
    try {
      String[] urlParts = urlPath.split("/");
      if (!isUrlValidStore(urlParts)) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().write("Invalid URL");
        return;
      } else {
        channel = connection.createChannel();
        String responseMessage = call("StoreId:" + urlParts[1]);
//        System.out.println(" [.] Got '" + responseMessage + "'");
        response.getWriter().write(responseMessage);
        response.setStatus(HttpServletResponse.SC_OK);
      }
    } catch (Exception e) {
      e.printStackTrace();
      //log.log(Level.SEVERE, "In PurchaseServlet" + e.getMessage());
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  public String call(String message) throws IOException, InterruptedException {
    final String corrId = UUID.randomUUID().toString();

    String replyQueueName = channel.queueDeclare().getQueue();
    System.out.println("ReplyQueue:" + replyQueueName);
    AMQP.BasicProperties props = new AMQP.BasicProperties
        .Builder()
        .correlationId(corrId)
        .replyTo(replyQueueName)
        .build();

    channel.basicPublish("", requestQueueName, props, message.getBytes(StandardCharsets.UTF_8));

    final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

    String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
      if (delivery.getProperties().getCorrelationId().equals(corrId)) {
        response.offer(new String(delivery.getBody(), "UTF-8"));
      }
    }, consumerTag -> {
    });

    String result = response.take();
    channel.basicCancel(ctag);
    return result;
  }

  private boolean isUrlValidStore(String[] urlParts) {
    //[, 28]
    //[, 32]
    if (urlParts == null || urlParts.length == 0 || urlParts.length > 2 || urlParts.length < 2) {
      return false;
    }
    if (!urlParts[1].isEmpty()) {
      try {
        Integer.parseInt(urlParts[1]);
      } catch (NumberFormatException e) {
        return false;
      }
    } else {
      return false;
    }
    return true;
  }

//  @Override
//  public void close() throws Exception {
//    connection.close();
//  }
}
