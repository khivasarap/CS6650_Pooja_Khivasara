package storeMicroservice;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.SerializationUtils;
import purchaseMicroservice.Purchase;

public class PurchaseConsumer {

  ExecutorService executor = Executors.newFixedThreadPool(100);
  ConnectionFactory factory = new ConnectionFactory();
  private final static String QUEUE_NAME_PURCHASE = "purchaseMQ";


  public PurchaseConsumer() throws IOException {
  }

  public void consumeAndPopulateMap(StoreService storeService)
      throws IOException, TimeoutException {
    factory.setHost("54.90.3.109");
    factory.setUsername("username");
    factory.setPassword("password");
//factory.setHost("localhost");
    final Connection connection = factory.newConnection();
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          final Channel channel = connection.createChannel();
          channel.exchangeDeclare(QUEUE_NAME_PURCHASE, "fanout");
          channel.queueDeclare(QUEUE_NAME_PURCHASE, true, false, false, null);
          channel.queueBind(QUEUE_NAME_PURCHASE, QUEUE_NAME_PURCHASE, "");
          // max one message per receiver
          //channel.basicQos(1);
          System.out.println(" [" + Thread.currentThread().getId()
              + "] Thread waiting for messages. To exit press CTRL+C");

          DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            Purchase purchase = SerializationUtils.deserialize(delivery.getBody());
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            // Populate Cache Data Structures for Get requests
            storeService.populateCacheDataStructure(purchase);
          };
          // process messages
          channel.basicConsume(QUEUE_NAME_PURCHASE, false, deliverCallback, consumerTag -> {
          });
        } catch (IOException ex) {
          Logger.getLogger(PurchaseConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    };
    // start threads and block to receive messages
    for (int i = 0; i < 10; i++) {
      executor.execute(
          () -> {
            Thread t = new Thread(runnable);
            t.start();
          });
    }
    executor.shutdown();
    //System.out.println(Arrays.toString(storeService.getTopNMostPurchasedItemsForStore(1,10).toArray()));
  }

}
