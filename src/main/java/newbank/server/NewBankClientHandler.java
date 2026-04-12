package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler implements Runnable {

  private NewBank bank;
  private BufferedReader in;
  private PrintWriter out;
  private Socket socket;

  public NewBankClientHandler(Socket s) throws IOException {
    this.socket = s;
    bank = NewBank.getBank();
    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    out = new PrintWriter(s.getOutputStream(), true);
  }

  @Override
  public void run() {
    CustomerID customer = null;
    try {
      while (true) {
        String request = in.readLine();
        if (request == null) break;

        // Mesajı parçalara ayır (LOGIN|user|pass|flag gibi)
        String[] parts = request.split("\\|");
        String command = parts[0];

        String response;
        if (command.equals("LOGIN")) {
          // Login veya Kayıt işlemi
          String user = parts[1];
          String pass = parts[2];
          boolean isNewUser = parts.length > 3 && parts[3].equals("newUser");

          // NewBank içindeki yeni login metodunu çağırıyoruz
          response = bank.handleLogin(user, pass, isNewUser);


          if (response.startsWith("SUCCESS")) {
            customer = new CustomerID(user);
          }
        } else {

          String args = (parts.length > 1) ? parts[1] : "";
          response = bank.processRequest(customer, command, args);
        }

        out.println(response);
        if (command.equals("EXIT")) break;
      }
    } catch (IOException e) {
      System.err.println("Bağlantı kesildi: " + e.getMessage());
    } finally {
      cleanup();
    }
  }
  private void cleanup() {
    try {
      in.close();
      out.close();
      socket.close();
    } catch (IOException e) {
      Thread.currentThread().interrupt();
    }
  }
}