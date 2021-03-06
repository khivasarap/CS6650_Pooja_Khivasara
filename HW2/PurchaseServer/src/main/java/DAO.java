import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAO {
  static Logger log = Logger.getLogger(String.valueOf(DAO.class));

  public static void insert(Purchase purchase) {
    String query = "INSERT INTO Purchase(StoreId,CustomerId,Date,body) VALUES(?,?,?,?)";
    try (Connection con = DataSource.getConnection()) {
      //System.out.println("All good");
      PreparedStatement ps = con.prepareStatement(query);
      ps.setInt(1, purchase.getStoreId());
      ps.setInt(2, purchase.getCustomerId());
      ps.setString(3, purchase.getDate());
      ps.setString(4, new Gson().toJson(purchase.getItems().getItems()));
      ps.executeUpdate();
    } catch (Exception e) {
      log.log(Level.SEVERE, "HereInsert "+e.getMessage());
    }
  }
}
