import java.util.Date;
import java.util.Objects;

public class Purchase {
  private Integer storeId;
  private Integer customerId;
  private String date;
  private PurchaseList items;

  public Purchase(Integer storeId, Integer customerId, String date, PurchaseList items) {
    this.storeId = storeId;
    this.customerId = customerId;
    this.date = date;
    setItems(items);
  }

  public Integer getStoreId() {
    return storeId;
  }

  public void setStoreId(Integer storeId) {
    this.storeId = storeId;
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public PurchaseList getItems() {
    return items;
  }

  public void setItems(PurchaseList items) {
    this.items = items;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Purchase purchase = (Purchase) o;
    return Objects.equals(storeId, purchase.storeId) &&
        Objects.equals(customerId, purchase.customerId) &&
        Objects.equals(date, purchase.date) &&
        Objects.equals(items, purchase.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(storeId, customerId, date, items);
  }
}
