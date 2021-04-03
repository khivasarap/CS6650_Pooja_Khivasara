package storeMicroservice;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.util.Arrays;

public class StoreRPCServer {
  private static final String RPC_QUEUE_NAME = "rpc_queue";
  public static void runStoreRPCServer() throws Exception{
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("54.90.3.109");
    factory.setUsername("username");
    factory.setPassword("password");
    //factory.setHost("localhost");
    try (Connection connection = factory.newConnection();
        Channel channel = connection.createChannel()) {
      channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
      channel.queuePurge(RPC_QUEUE_NAME);

      channel.basicQos(1);

      System.out.println(" [x] Awaiting RPC requests");

      Object monitor = new Object();
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        AMQP.BasicProperties replyProps = new AMQP.BasicProperties
            .Builder()
            .correlationId(delivery.getProperties().getCorrelationId())
            .build();

        String response = "";

        try {
          String message = new String(delivery.getBody(), "UTF-8");
          //int n = Integer.parseInt(message);

          System.out.println("Printing:"+ message);
          if(message.startsWith("StoreId:")) {
            int storeId = Integer.parseInt(message.split(":")[1]);
            //response+= Arrays.toString(StoreService.getTopNMostPurchasedItemsForStore(storeId,10).toArray());
            response+= StoreService.getTopNMostPurchasedItemsForStore(storeId,10);
            //Get for storeId is received and generate response accordingly.
          } else if(message.startsWith("ItemId:")) {
            int itemId = Integer.parseInt(message.split(":")[1]);
            //response+= Arrays.toString(StoreService.getTopNStoresForItem(itemId,5).toArray());
            response+=StoreService.getTopNStoresForItem(itemId,5);
          }
          //response += "Got responseeer";
        } catch (RuntimeException | NoItemsFoundException | NoStoreContainsItemException e) {
          System.out.println(" [.] " + e.toString());
        } finally {
          channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
          // RabbitMq consumer worker thread notifies the RPC server owner thread
          synchronized (monitor) {
            monitor.notify();
          }
        }
      };

      channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
      // Wait and be prepared to consume the message from RPC client.
      while (true) {
        synchronized (monitor) {
          try {
            monitor.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
}
