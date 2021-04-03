package storeMicroservice;

import java.util.Objects;

public class ItemObject {
  private Integer itemId;
  private int numberOfItems;

  public ItemObject(int itemId, int numberOfItems) {
    this.itemId = itemId;
    this.numberOfItems = numberOfItems;
  }

  public Integer getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public int getNumberOfItems() {
    return numberOfItems;
  }

  public void setNumberOfItems(int numberOfItems) {
    this.numberOfItems = numberOfItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ItemObject that = (ItemObject) o;
    return itemId == that.itemId &&
        numberOfItems == that.numberOfItems;
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemId, numberOfItems);
  }
}
