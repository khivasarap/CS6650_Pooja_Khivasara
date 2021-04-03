package storeMicroservice;

public class StoreObject {
  private int storeId;
  private int numberOfItems;

  public StoreObject(int storeId, int numberOfItems) {
    this.storeId = storeId;
    this.numberOfItems = numberOfItems;
  }

  public int getStoreId() {
    return storeId;
  }

  public void setStoreId(int storeId) {
    this.storeId = storeId;
  }

  public int getNumberOfItems() {
    return numberOfItems;
  }

  public void setNumberOfItems(int numberOfItems) {
    this.numberOfItems = numberOfItems;
  }
}
