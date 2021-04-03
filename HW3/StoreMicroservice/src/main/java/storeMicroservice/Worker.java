package storeMicroservice;

import purchaseMicroservice.Purchase;

public class Worker implements Runnable {

  @Override
  public void run() {
    while (true) {
      if (!StoreService.purchaseList.isEmpty()) {
        Purchase p = StoreService.purchaseList.remove(0);
        StoreService.populateMostPurchased(p);
        StoreService.populateTopStoresMap(p);
      }
    }
  }
}
