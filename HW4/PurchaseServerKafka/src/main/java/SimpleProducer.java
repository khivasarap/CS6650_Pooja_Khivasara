import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import purchaseMicroservice.Purchase;
import purchaseMicroservice.PurchaseItems;
import purchaseMicroservice.PurchaseList;

public class SimpleProducer {
  public static void main(String[] args) throws InterruptedException {
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("acks", "all");
    props.put("retries", 0);
    props.put("batch.size", 16384);
    props.put("linger.ms", 1);
    props.put("buffer.memory", 33554432);
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
    PurchaseItems item1 = new PurchaseItems("71602",1);
    List<PurchaseItems> list = new ArrayList<>();
    list.add(item1);
    Purchase purchase = new Purchase(1,1,"2010-10-01",new PurchaseList(list));
    byte[] purchaseData = SerializationUtils.serialize(purchase);
    Producer<String, byte[]> producer = new KafkaProducer<>(props);
    for(int i = 0; i < 10; i++)
      producer.send(new ProducerRecord<String, byte[]>("mytest", Integer.toString(i),purchaseData ));

    producer.close();

  }}