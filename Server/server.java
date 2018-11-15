/*
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class server {

  public static void main(String[] args) throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
    server.createContext("/info", new InfoHandler());
    server.createContext("/get", new GetHandler());
    server.setExecutor(null); // creates a default executor
    server.start();
    System.out.println("Server is listening on port 8080" );
  }

  static class InfoHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
      String response = "Use /get to download a PDF";
      t.sendResponseHeaders(200, response.length());
      OutputStream os = t.getResponseBody();
      os.write(response.getBytes());
      os.close();
    }
  }

  static class GetHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {

      // add the required response header for a PDF file
      Headers h = t.getResponseHeaders();
      h.add("Content-Type", "application/pdf");

      // a PDF (you provide your own!)
      File file = new File ("c:/Users/pmcdo/test.pdf");
      byte [] bytearray  = new byte [(int)file.length()];
      FileInputStream fis = new FileInputStream(file);
      BufferedInputStream bis = new BufferedInputStream(fis);
      bis.read(bytearray, 0, bytearray.length);

      // ok, we are ready to send the response.
      t.sendResponseHeaders(200, file.length());
      OutputStream os = t.getResponseBody();
      os.write(bytearray,0,bytearray.length);
      os.close();
    }
  }
  
//HTTP POST request
  private void sendPost() throws Exception {

  String url = "http://localhost:8080/requests";
  URL obj = new URL(url);
  HttpURLConnection con = (HttpURLConnection) obj.openConnection();

  //add reuqest header
  con.setRequestMethod("POST");
  con.setRequestProperty("User-Agent", USER_AGENT);
  con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

  String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

  // Send post request
  con.setDoOutput(true);
  DataOutputStream wr = new DataOutputStream(con.getOutputStream());
  wr.writeBytes(urlParameters);
  wr.flush();
  wr.close();

  int responseCode = con.getResponseCode();
  System.out.println("\nSending 'POST' request to URL : " + url);
  System.out.println("Post parameters : " + urlParameters);
  System.out.println("Response Code : " + responseCode);

  BufferedReader in = new BufferedReader(
          new InputStreamReader(con.getInputStream()));
  String inputLine;
  StringBuffer response = new StringBuffer();

  while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
  }
  in.close();

  //print result
  System.out.println(response.toString());

}
  
  
  
  
}*/