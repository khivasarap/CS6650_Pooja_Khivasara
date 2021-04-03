import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import purchaseMicroservice.Purchase;

public class DAO {
  static Logger log = Logger.getLogger(String.valueOf(DAO.class));

  public static void insert(Purchase purchase) {
    String query = "INSERT INTO Purchase(StoreId,CustomerId,Date,body) VALUES(?,?,?,?)";
    try {
      Connection con = DataSource.getConnection();
      PreparedStatement ps = con.prepareStatement(query);
      ps.setInt(1, purchase.getStoreId());
      ps.setInt(2, purchase.getCustomerId());
      ps.setString(3, purchase.getDate());
      ps.setString(4, new ObjectMapper().writeValueAsString(purchase.getItems().getItems()));
      ps.executeUpdate();
      ps.close();
      con.close();
    } catch (Exception e) {
      log.log(Level.SEVERE, "Failed while inserting into DB "+e.getMessage());
    }
  }
  public static void insertBatch(List<Purchase> purchaseList) {
    try {
    Connection con = DataSource.getConnection();
    String query = "INSERT INTO Purchase(StoreId,CustomerId,Date,body) VALUES(?,?,?,?)";
      PreparedStatement ps = con.prepareStatement(query);
      for(Purchase purchase: purchaseList){
        ps.setInt(1, purchase.getStoreId());
        ps.setInt(2, purchase.getCustomerId());
        ps.setString(3, purchase.getDate());
        ps.setString(4, new ObjectMapper().writeValueAsString(purchase.getItems().getItems()));
        ps.addBatch();
      }
      ps.executeBatch();
      ps.close();
      con.close();
    } catch (Exception e) {
      log.log(Level.SEVERE, "Failed while inserting into DB "+e.getMessage());
    }
  }
}
