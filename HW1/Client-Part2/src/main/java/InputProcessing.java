import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputProcessing {
  private static int NUM_OF_CUSTOMERS_PER_STORE = 1000;
  private static int MAX_ITEM_ID = 100000;
  private static int NUM_PURCHASES_PER_HOUR = 60;
  private static int NUM_PURCHASES_PER_PURCHASE = 5;
  private static String DATE = "20210101";
  private static String FILE_NAME = "config.properties";
  static Logger log = Logger.getLogger(String.valueOf(InputProcessing.class));

  public static InputParameter processInput() {
    InputStream inputStream;
    Properties properties = new Properties();

    try {
        String fileName = FILE_NAME;
        ClassLoader classLoader = PurchaseApiClient.class.getClassLoader();
        inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream != null) {
          properties.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + fileName + "' not found in the classpath");
        }
      // load the properties file
      properties.load(inputStream);
      boolean isValidInput = validateInput(properties);
      if(!isValidInput) {
        throw new IllegalArgumentException("Invalid InputParameter. Please check config.properties");
      }
      return loadInputParameterToObject(properties);
    } catch (FileNotFoundException e) {
      log.log(Level.WARNING, e.getMessage());
    } catch (IOException e) {
      log.log(Level.WARNING, e.getMessage());
    }
    return new InputParameter();
  }

  private static InputParameter loadInputParameterToObject(Properties properties) {
    InputParameter parameter = new InputParameter();
    parameter.setMaxStores(Integer.valueOf(properties.getProperty("maxStores")));
    parameter.setNumberOfCustomers(Integer.valueOf(properties.getProperty("numberOfCustomers")));
    parameter.setMaxItemId(Integer.valueOf(properties.getProperty("maxItemId")));
    parameter.setNumPurchases(Integer.valueOf(properties.getProperty("numPurchases")));
    parameter.setNumberOfItemsPerPurchase(Integer.valueOf(properties.getProperty("numberOfItemsPerPurchase")));
    parameter.setDate(properties.getProperty("date"));
    parameter.setIpAndPort(properties.getProperty("ip"));
    return parameter;
  }

  public static boolean validateInput(Properties properties) {
    if(properties!=null && !properties.isEmpty()) {
      if(properties.getProperty("maxStores").isEmpty()){
        return false;
      }
      if(properties.getProperty("numberOfCustomers").isEmpty()) {
        properties.setProperty("numberOfCustomers", String.valueOf(NUM_OF_CUSTOMERS_PER_STORE));
      }
      if(properties.getProperty("maxItemId").isEmpty()) {
        properties.setProperty("maxItemId", String.valueOf(MAX_ITEM_ID));
      }
      if(properties.getProperty("numPurchases").isEmpty()) {
        properties.setProperty("numPurchases", String.valueOf(NUM_PURCHASES_PER_HOUR));
      }
      if(properties.getProperty("numberOfItemsPerPurchase").isEmpty()) {
        properties.setProperty("numberOfItemsPerPurchase", String.valueOf(NUM_PURCHASES_PER_PURCHASE));
      } else if(Integer.valueOf(properties.getProperty("numberOfItemsPerPurchase")) < 1 || Integer.valueOf(properties.getProperty("numberOfItemsPerPurchase")) > 20) {
        return false;
      }
      if(properties.getProperty("date").isEmpty()){
        properties.setProperty("date",DATE);
      }
      if(properties.getProperty("ip").isEmpty()){
        return false;
      }
    }
    return true;
  }
}
