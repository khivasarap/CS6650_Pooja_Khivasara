package storeMicroservice;

public class NoItemsFoundException extends Exception {

  public NoItemsFoundException(String message) {
    super(message);
  }
}
