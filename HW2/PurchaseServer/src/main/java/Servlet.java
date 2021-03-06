import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;

public class Servlet extends javax.servlet.http.HttpServlet {
  static Logger log = Logger.getLogger(String.valueOf(Servlet.class));

  protected void doPost(javax.servlet.http.HttpServletRequest request,
      javax.servlet.http.HttpServletResponse response)
      throws javax.servlet.ServletException, IOException {
    response.setContentType("text/plain");
    String urlPath = request.getPathInfo();

    // check if URL exists
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("missing parameters");
      return;
    }
    try {
      String[] urlParts = urlPath.split("/");
      if (!isUrlValid(urlParts)) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.getWriter().write("Invalid URL");
      } else {
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = request.getReader().readLine()) != null) {
          sb.append(s);
        }
        Gson gson = new Gson();
        PurchaseList purchaseList = (PurchaseList) gson.fromJson(sb.toString(), PurchaseList.class);
        //System.out.println(sb.toString());
        //System.out.println(purchaseList.getItems())
//        PurchaseList purchaseList = new PurchaseList(new ArrayList<PurchaseItems>());
        Purchase purchase = new Purchase(Integer.parseInt(urlParts[1]),
            Integer.parseInt(urlParts[3]), urlParts[5], purchaseList);
        DAO.insert(purchase);
        response.setStatus(HttpServletResponse.SC_CREATED);
      }
    } catch(Exception e){
      log.log(Level.SEVERE, "InTestServlet"+e.getMessage());
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  protected void doGet(javax.servlet.http.HttpServletRequest request,
      javax.servlet.http.HttpServletResponse response)
      throws javax.servlet.ServletException, IOException {

  }
  private boolean isUrlValid(String[] urlParts) {
    //[, 28, customer, 28578, date, 20210101]
    //[, 32, customer, 32066, date, 20210101]
    if (urlParts == null || urlParts.length == 0 || urlParts.length > 6 || urlParts.length < 6) {
      return false;
    }
    if (!urlParts[1].isEmpty() && !urlParts[3].isEmpty() && !urlParts[5].isEmpty()) {
      try {
        Integer.parseInt(urlParts[1]);
        Integer.parseInt(urlParts[3]);
        if (urlParts[5].length() < 8) {
          return false;
        }
      } catch (NumberFormatException e) {
        return false;
      }
    }
    if (!urlParts[2].isEmpty() && !urlParts[4].isEmpty()) {
      if (!"customer".equalsIgnoreCase(urlParts[2])) {
        return false;
      }
      if (!"date".equalsIgnoreCase(urlParts[4])) {
        return false;
      }
    }
    return true;
  }
}
