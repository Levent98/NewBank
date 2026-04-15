package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class NewBankClientHandler extends Thread {

  private static final int SESSION_TIMEOUT_MS = 300000; // 5 minutes

  private final NewBank bank;
  private final BufferedReader in;
  private final PrintWriter out;
  private final Socket socket;

  public NewBankClientHandler(Socket s) throws IOException {
    this.socket = s;
    this.bank = NewBank.getBank();
    this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    this.out = new PrintWriter(s.getOutputStream(), true);

    // No timeout before login
    this.socket.setSoTimeout(0);
  }

  @Override
  public void run() {
    CustomerID customer = null;

    try {
      while (true) {
        String request;

        try {
          request = in.readLine();
        } catch (SocketTimeoutException e) {
          if (customer != null) {
            System.out.println("Session timed out for user: " + customer.getKey());
            customer = null;
            out.println("LOGGED OUT - Session timed out");

            // Back to pre-login state
            socket.setSoTimeout(0);
            continue;
          } else {
            socket.setSoTimeout(0);
            continue;
          }
        }

        if (request == null) {
          break;
        }

        String[] requestParsed = request.split("\\|", 2);
        String command = requestParsed[0];
        String args = (requestParsed.length > 1) ? requestParsed[1] : "";
        String response;

        if ("LOGIN".equals(command)) {
          String[] parts = args.split("\\|", 3);

          if (parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            response = "FAIL";
          } else {
            String username = parts[0];
            String password = parts[1];

            if (parts.length == 3 && "newUser".equals(parts[2])) {
              response = bank.createLogInDetails(username, password);
              if (!response.startsWith("SUCCESS")) {
                out.println(response);
                continue;
              }
            }

            customer = bank.checkLogInDetails(username, password);
            if (customer != null) {
              System.out.println("User Login: " + username);
              response = customer.isEmployee() ? "SUCCESS EMPLOYEE" : "SUCCESS";

              // Start timeout only after successful login
              socket.setSoTimeout(SESSION_TIMEOUT_MS);
            } else {
              System.out.println("Failed user login: " + username);
              response = "FAIL";
            }
          }

        } else if ("CHANGEPW".equals(command)) {
          if (customer == null) {
            response = "Please login first";
          } else if (args.contains("|")) {
            response = "FAIL";
          } else {
            response = bank.changeLogInPassword(customer.getKey(), args);
            socket.setSoTimeout(SESSION_TIMEOUT_MS);
          }

        } else if ("LOGOUT".equals(command)) {
          if (customer != null) {
            System.out.println("User Logout: " + customer.getKey());
          }
          customer = null;
          response = "LOGGED OUT";
          socket.setSoTimeout(0);

        } else {
          if (customer == null) {
            response = "Please login first";
          } else {
            response = bank.processRequest(customer, command, args);
            socket.setSoTimeout(SESSION_TIMEOUT_MS);
          }
        }

        out.println(response);
      }

    } catch (IOException e) {
      System.out.println("Client disconnected");
    } finally {
      try {
        in.close();
        out.close();
        socket.close();
      } catch (IOException e) {
        Thread.currentThread().interrupt();
      }
    }
  }
}