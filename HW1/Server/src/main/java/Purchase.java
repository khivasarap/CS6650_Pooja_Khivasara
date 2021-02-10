import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Purchase {
  private List<PurchaseItems> items = null;

  public Purchase(List<PurchaseItems> items) {
    this.items = items;
  }

  public List<PurchaseItems> getItems() {
    return items;
  }

  public void setItems(List<PurchaseItems> items) {
    this.items = items;
  }
  public Purchase addItemsItem(PurchaseItems itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<PurchaseItems>();
    }
    this.items.add(itemsItem);
    return this;
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
    return Objects.equals(items, purchase.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(items);
  }
}
