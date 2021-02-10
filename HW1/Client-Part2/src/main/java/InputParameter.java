public class InputParameter {
  private Integer maxStores;
  private Integer numberOfCustomers;
  private Integer maxItemId;
  private Integer numPurchases;
  private Integer numberOfItemsPerPurchase;
  private String date;
  private String ipAndPort;

  public InputParameter() {
  }

  public Integer getMaxStores() {
    return maxStores;
  }

  public void setMaxStores(Integer maxStores) {
    this.maxStores = maxStores;
  }

  public Integer getNumberOfCustomers() {
    return numberOfCustomers;
  }

  public void setNumberOfCustomers(Integer numberOfCustomers) {
    this.numberOfCustomers = numberOfCustomers;
  }

  public Integer getMaxItemId() {
    return maxItemId;
  }

  public void setMaxItemId(Integer maxItemId) {
    this.maxItemId = maxItemId;
  }

  public Integer getNumPurchases() {
    return numPurchases;
  }

  public void setNumPurchases(Integer numPurchases) {
    this.numPurchases = numPurchases;
  }

  public Integer getNumberOfItemsPerPurchase() {
    return numberOfItemsPerPurchase;
  }

  public void setNumberOfItemsPerPurchase(Integer numberOfItemsPerPurchase) {
    this.numberOfItemsPerPurchase = numberOfItemsPerPurchase;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getIpAndPort() {
    return ipAndPort;
  }

  public void setIpAndPort(String ipAndPort) {
    this.ipAndPort = ipAndPort;
  }
}
