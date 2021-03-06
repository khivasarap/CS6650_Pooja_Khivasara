package purchaseMicroservice;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Objects;

public class PurchaseItems implements Serializable {
  @SerializedName("ItemID")
  private String itemID;
  @SerializedName("numberOfItems:")
  private Integer numberOfItems;

  public PurchaseItems(String itemID, Integer numberOfItems)  {
    this.itemID = itemID;
    this.numberOfItems = numberOfItems;
  }

  public String getItemID() {
    return itemID;
  }

  public void setItemID(String itemID) {
    this.itemID = itemID;
  }

  public Integer getNumberOfItems() {
    return numberOfItems;
  }

  public void setNumberOfItems(Integer numberOfItems) {
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
    PurchaseItems that = (PurchaseItems) o;
    return Objects.equals(itemID, that.itemID) &&
        Objects.equals(numberOfItems, that.numberOfItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemID, numberOfItems);
  }
}
