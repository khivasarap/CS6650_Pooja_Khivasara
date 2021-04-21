package purchaseMicroservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PurchaseList implements Serializable {
  private List<PurchaseItems> items = null;
  public PurchaseList(List<PurchaseItems> items) {
    this.items = items;
  }

  public List<PurchaseItems> getItems() {
    return items;
  }

  public void setItems(List<PurchaseItems> items) {
    this.items = items;
  }
  public PurchaseList addItemsItem(PurchaseItems itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<PurchaseItems>();
    }
    this.items.add(itemsItem);
    return this;
  }
}
