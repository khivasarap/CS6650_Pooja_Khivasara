package storeMicroservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import purchaseMicroservice.Purchase;
import purchaseMicroservice.PurchaseItems;

public class StoreService {

  static ConcurrentHashMap<Integer, HashMap<Integer, Integer>> mostPurchased = new ConcurrentHashMap<>();
  static ConcurrentHashMap<Integer, HashMap<Integer, Integer>> topStores = new ConcurrentHashMap<>();
  static List<Purchase> purchaseList = Collections.synchronizedList(new ArrayList<>());
  static ExecutorService executorWorker = Executors.newFixedThreadPool(10);

  public StoreService() {
  }

  public static void populateCacheDataStructure2(Purchase purchase) {
    populateList(purchase);
    for (int i = 0; i < 10; i++) {
      Worker worker = new Worker();
      executorWorker.execute(worker);
    }
    executorWorker.shutdown();
  }

  public static void populateCacheDataStructure(Purchase p) {
    StoreService.populateMostPurchased(p);
    StoreService.populateTopStoresMap(p);
  }


  private static void populateList(Purchase purchase) {
    purchaseList.add(purchase);
  }

  public static void populateMostPurchased(Purchase purchase) {
    Integer storeId = purchase.getStoreId();
    List<PurchaseItems> purchaseList = purchase.getItems().getItems();
    for (PurchaseItems item : purchaseList) {
      if (mostPurchased.containsKey(storeId)) {
        int itemId = Integer.parseInt(item.getItemID());
        if (mostPurchased.get(storeId).containsKey(itemId)) {
          HashMap<Integer, Integer> itemMap = mostPurchased.get(storeId);
          itemMap.put(itemId, itemMap.get(itemId) + item.getNumberOfItems());
          mostPurchased.put(storeId, itemMap);
        } else {
          HashMap<Integer, Integer> itemMap = mostPurchased.get(storeId);
          itemMap.put(itemId, item.getNumberOfItems());
          mostPurchased.put(storeId, itemMap);
        }
      } else {
        mostPurchased.put(storeId, new HashMap<Integer, Integer>(Integer.parseInt(item.getItemID()),
            item.getNumberOfItems()));
      }
    }
  }

  public static void populateTopStoresMap(Purchase purchase) {
    Integer storeId = purchase.getStoreId();
    List<PurchaseItems> purchaseList = purchase.getItems().getItems();
    for (PurchaseItems item : purchaseList) {
      int itemId = Integer.parseInt(item.getItemID());
      if (topStores.containsKey(itemId)) {
        HashMap<Integer, Integer> existingStoreItemCount = topStores.get(itemId);
        if (topStores.get(itemId).containsKey(storeId)) {
          existingStoreItemCount.put(storeId,
              existingStoreItemCount.get(storeId).intValue() + item.getNumberOfItems().intValue());
        } else {
          existingStoreItemCount.put(storeId, item.getNumberOfItems());
        }
        topStores.put(itemId, existingStoreItemCount);
      } else {
        HashMap<Integer, Integer> storeMap = new HashMap<>();
        storeMap.put(storeId, item.getNumberOfItems());
        topStores.put(itemId, storeMap);
      }
    }
  }

  public static String getTopNMostPurchasedItemsForStore(int storeId, int N)
      throws NoItemsFoundException, JsonProcessingException {
    //List<Integer> topItems = new ArrayList<>();
    String resultJson = "";
    List<ItemObject> topItems = new ArrayList<>();
    HashMap<Integer, Integer> items = mostPurchased.get(storeId);
    if (items == null || items.isEmpty()) {
      throw new NoItemsFoundException("No Items found for the Store " + storeId);
    } else {
      getTopItemsFromDataStructure(N, topItems, items);
      ObjectMapper objectMapper = new ObjectMapper();
      Map<String, List<ItemObject>> map = new HashMap<String, List<ItemObject>>();
      map.put("stores", topItems);
      resultJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
    }
    return resultJson;
  }

  public static String getTopNStoresForItem(int itemId, int N)
      throws NoStoreContainsItemException, JsonProcessingException {
    String resultJson = "";
    List<StoreObject> topStore = new ArrayList<>();
    List<Integer> result = new ArrayList<>();
    HashMap<Integer, Integer> stores = topStores.get(itemId);
    if (stores == null || stores.isEmpty()) {
      throw new NoStoreContainsItemException("No store sales for item.");
    } else {
      getTopStoreFromDataStructure(N, topStore, stores);
      ObjectMapper objectMapper = new ObjectMapper();
      Map<String, List<StoreObject>> map = new HashMap<String, List<StoreObject>>();
      map.put("stores", topStore);
      resultJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
    }
    return resultJson;
  }

  private static void getTopItemsFromDataStructure(int N, List<ItemObject> resultItems,
      HashMap<Integer, Integer> stores) {
    PriorityQueue<Integer> pq = new PriorityQueue<>(
        (a, b) -> stores.get(a).equals(stores.get(b)) ? a.compareTo(b)
            : stores.get(b) - stores.get(a));
    pq.addAll(stores.keySet());
    while (N > 0) {
      int itemId = pq.remove();
      resultItems.add(new ItemObject(itemId, stores.get(itemId)));
      N--;
    }
  }

  private static void getTopStoreFromDataStructure(int N, List<StoreObject> resultItems,
      HashMap<Integer, Integer> stores) {
    PriorityQueue<Integer> pq = new PriorityQueue<>(
        (a, b) -> stores.get(a).equals(stores.get(b)) ? a.compareTo(b)
            : stores.get(b) - stores.get(a));
    pq.addAll(stores.keySet());
    while (N > 0) {
      int storeId = pq.remove();
      resultItems.add(new StoreObject(storeId, stores.get(storeId)));
      N--;
    }
  }
}
