import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import purchaseMicroservice.Purchase;

public class SimpleConsumer {

  public static void main(String[] args) {
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("group.id", "test");
//    props.put("zookeeper.connect", "localhost:2181");
    props.put("enable.auto.commit", "true");
    props.put("auto.commit.interval.ms", "1000");
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
    KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);
    //consumer.subscribe(Arrays.asList("topic3", "topic2"));
    consumer.subscribe(Collections.singleton("mytest"));
    while (true) {
      ConsumerRecords<String, byte[]> records = consumer.poll(Long.MAX_VALUE);
      for (ConsumerRecord<String, byte[]> record : records) {
        Purchase purchase = SerializationUtils.deserialize(record.value());
        System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), purchase.toString());
      }
    }
  }}