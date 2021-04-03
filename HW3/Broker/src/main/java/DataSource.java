import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataSource {

  private static HikariConfig config = new HikariConfig();
  private static HikariDataSource ds;
  static Logger log = Logger.getLogger(String.valueOf(DataSource.class));

  static {
    try {
      config.setDriverClassName("com.mysql.cj.jdbc.Driver");
//      config.setJdbcUrl( "jdbc:mysql://localhost:3306/databasePooja" );
      config.setJdbcUrl(
          "jdbc:mysql://databasepooja.c8tteeyvfh24.us-east-1.rds.amazonaws.com:3306/databasePooja");
      config.setUsername("admin");
      config.setPassword("password");

      config.addDataSourceProperty("cachePrepStmts", "true");
      //250
      config.addDataSourceProperty("prepStmtCacheSize", "500");
      config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
      config.setConnectionTimeout(60000);
      //Total number of concurrent connections
      config.setMaximumPoolSize(10);
      config.setMinimumIdle(5);
      ds = new HikariDataSource(config);
    } catch (Exception e) {
      e.printStackTrace();
      log.log(Level.SEVERE, "DataSource" + e.getMessage());
    }
  }

  public static Connection getConnection() throws SQLException {
    return ds.getConnection();
  }
}
