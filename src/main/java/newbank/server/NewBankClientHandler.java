// once client server interaction has established socket between clietnhandler and client this class is used to handle I/o via send/response commands with ExampleClient.
// Information arriving here can be taken out for business logic (NewBank) etc to handle banking operations.
// Return responses can either act as keyword trigger logic in UI or simple dsiplay the reponse back to the user terminal.

package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread {

  private NewBank bank;
  private BufferedReader in;
  private PrintWriter out;

  public NewBankClientHandler(Socket s) throws IOException {
    bank = NewBank.getBank();
    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    out = new PrintWriter(s.getOutputStream(), true);
  }

  @Override
  public void run() {
    CustomerID customer = null;
    try {
      while (true) {
        // read request from client
        String request = in.readLine();
        if (request == null) {
          break;
        }
        String response; // response is used for pairing in UI to handle UI case 1 or 2 (login or UI menu)

        // Login with keyword to distinuish UI state from menu command
        if (request.startsWith("LOGIN")) {
          String[] parts = request.split(" ");
          if (parts.length < 3) {
            response = "FAIL"; // at UI login entry both username and password should be entered to satisfy requirements (keyword LOGIN counts as 1 part here)
          } else {
            String username = parts[1];
            String password = parts[2];

            // Adding creation flow
            if (parts.length == 4 && "newUser".equals(parts[3])) {
              response = bank.createLogInDetails(username, password);
              if (!response.startsWith("SUCCESS")) {
                out.println(response);
                continue;
              }
            }

            // Authentication via NewBank method
            customer = bank.checkLogInDetails(username, password);
            if (customer != null) {
              System.out.println("User Login: " + username); // prints to bank side terminal
              response = customer.isEmployee() ? "SUCCESS EMPLOYEE" : "SUCCESS";
            } else {
              System.out.println("Failed user login: " + username); // prints to bank side temrinal
              response = "FAIL"; // response to UI
            }
          }
        } else if (request.startsWith("CHANGEPW")) {
          String[] parts = request.split(" ");
          response = "FAIL";
          if (parts.length == 2) {
            response = bank.changeLogInPassword(customer.getKey(), parts[1]);
          }
        } else if (request.equals("LOGOUT")) {
          // Log out handling, upon logout bank terminal notified, customer session ends, then logout repsonse sent to UI (cannot cause crash if LOGOUT attempted while no customer in event of client/server error)
          if (customer != null) {
            System.out.println("User Logout: " + customer.getKey());
          }
          customer = null;
          response = "LOGGED OUT";
        }

        // Other commands passed to NewBank with customer name identifier
        else {
          if (customer == null) {
            response = "Please login first";
          } else {
            response = bank.processRequest(customer, request);
          }
        }   
        // send response back to bank terminal
        out.println(response);
      }

    } catch (IOException e) {
      System.out.println("Client disconnected");
    } finally {
      try {
        in.close();
        out.close();
      } catch (IOException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

}
