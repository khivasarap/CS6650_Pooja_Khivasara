import storeMicroservice.PurchaseConsumer;
import storeMicroservice.StoreRPCServer;
import storeMicroservice.StoreService;

public class StoreApplication {

  public static void main(String args[]) throws Exception {
    // Call PurchaseConsumer to update Data structure
    StoreService storeService = new StoreService();
    new PurchaseConsumer().consumeAndPopulateMap(storeService);
    // Call StoreRPCServer so that it can consume from queue (request) and send back the response
    new StoreRPCServer().runStoreRPCServer();
  }
}
