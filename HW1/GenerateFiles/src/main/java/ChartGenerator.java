import java.io.*;
import java.util.Scanner;
import java.io.FileWriter;
class ChartGenerator {
  public static void main(String[] args) throws Exception   {
    FileWriter csv = new FileWriter("chart_256.csv");
    //parsing a CSV file into Scanner class constructor  
    Scanner sc = new Scanner(new File("C:\\Users\\Modak\\Desktop\\NEU\\Term 4- Spring 2021\\CS 6650 - Distributed Scalable Systems\\GenerateFiles\\src\\main\\resources\\output_256.csv"));
    sc.useDelimiter(",");   //sets the delimiter pattern 
    long fistValue = Long.parseLong(sc.next());
    long previousValue = -1;
    double latency = 0.0;
    int count = 1;
    System.out.println("here: "+fistValue);
    boolean i = true;
    while (sc.hasNext())  //returns a boolean value  
    {
      if(i){
        String nextLine = sc.next();
        String[] x = nextLine.split("\n");

        latency = latency + Double.parseDouble(x[0]);
        //i = false;
        long currentValue =0;
        if(x.length > 1)
          currentValue = Long.parseLong(x[1]);
        if(currentValue < fistValue+1000){
          //within 1 sec 
          i = true;
          count++;
        }
        else {
          csv.append(""+fistValue);
          csv.append(",");
          csv.append(""+latency/(count*1.0));
          csv.append("\n");
          fistValue = currentValue;
          count = 1;
          latency = 0.0;
          i = true;
        }
      }


    }
    csv.flush();
    csv.close();
    sc.close();  //closes the scanner  
  }
}