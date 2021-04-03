import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.SerializationUtils;
import purchaseMicroservice.Purchase;

public class Recv {

  private final static String QUEUE_NAME = "rabbitMQ";
  private static List<Purchase> purchaseList = Collections.synchronizedList(new ArrayList<>());

  public static void main(String[] argv) throws Exception {
    ExecutorService executor = Executors.newFixedThreadPool(100);
    ExecutorService executorWorker = Executors.newFixedThreadPool(100);

    ExecutorService executor2 = Executors.newFixedThreadPool(1000);
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("18.207.253.144");
    factory.setUsername("username");
    factory.setPassword("password");
    //factory.setHost("localhost");

    Connection connection = factory.newConnection(executor2);
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          Channel channel = connection.createChannel();
          channel.exchangeDeclare(QUEUE_NAME, "fanout");
          channel.queueDeclare(QUEUE_NAME, true, false, false, null);
          //channel.queueBind(QUEUE_NAME, QUEUE_NAME, "");
          //System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
          // max one message per receiver
          channel.basicQos(5);
          DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            byte[] body = delivery.getBody();
            try {
              Purchase purchase = SerializationUtils.deserialize(body);
              channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
              //DAO.insert(purchase);
//              if (purchaseList.size() >= 32) {
//                DAO.insertBatch(purchaseList);
//                purchaseList = Collections.synchronizedList(new ArrayList<>());
//              }
              purchaseList.add(purchase);
            } catch (Exception e) {
              e.printStackTrace();
            }
          };
          channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
          });
        } catch (IOException ex) {
          Logger.getLogger(Recv.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    };
    Runnable runnableWorker = new Runnable() {
      @Override
      public void run() {
        while (true) {
          if (!purchaseList.isEmpty()) {
            Purchase p = purchaseList.remove(0);
            DAO.insert(p);
          }
//          else {
//            try {
//              Thread.sleep(1);
//            } catch (InterruptedException e) {
//              e.printStackTrace();
//            }
//          }
        }
      }
    };
    // start threads and block to receive messages
    for (int i = 0; i < 100; i++) {
      Thread recv1 = new Thread(runnable);
      executor.execute(recv1);
      Thread worker = new Thread(runnableWorker);
      executorWorker.execute(worker);
    }
//    executor.execute(
//        () -> {
//          Thread t = new Thread(runnable);
//          t.start();
//        });

    executor.shutdown();

  }

}