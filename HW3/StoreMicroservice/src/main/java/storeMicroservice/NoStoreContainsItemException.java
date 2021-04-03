package storeMicroservice;

public class NoStoreContainsItemException extends Exception {

  public NoStoreContainsItemException(String message) {
    super(message);
  }
}
