package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException; // Required for Requirement 18

public class NewBankClientHandler implements Runnable {

  private NewBank bank;
  private BufferedReader in;
  private PrintWriter out;
  private Socket socket;

  public NewBankClientHandler(Socket s) throws IOException {
    this.socket = s;

    // Requirement 18.1: Set timeout to 30 seconds
    this.socket.setSoTimeout(30000);

    bank = NewBank.getBank();
    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    out = new PrintWriter(s.getOutputStream(), true);
  }

  @Override
  public void run() {
    CustomerID customer = null;
    try {
      while (true) {
        // readLine() will throw SocketTimeoutException if no input for 30 seconds
        String request = in.readLine();
        if (request == null) break;

        String[] parts = request.split("\\|");
        String command = parts[0];

        String response;
        if (command.equals("LOGIN")) {
          String user = parts[1];
          String pass = parts[2];
          boolean isNewUser = parts.length > 3 && parts[3].equals("newUser");

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
    } catch (SocketTimeoutException e) {
      // Requirement 18.2: Notify the user with the exact message
      out.println("LOGGED OUT - Session timed out");
      System.out.println("[SECURITY] Session timed out for " + socket.getRemoteSocketAddress());
    } catch (IOException e) {
      System.err.println("Bağlantı kesildi: " + e.getMessage());
    } finally {
      // Requirement 18.4: Ensure further commands are blocked by closing the socket
      cleanup();
    }
  }

  private void cleanup() {
    try {
      if (in != null) in.close();
      if (out != null) out.close();
      if (socket != null) socket.close();
    } catch (IOException e) {
      Thread.currentThread().interrupt();
    }
  }
}