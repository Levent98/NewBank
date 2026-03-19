// once client server interaction has established socket between clietnhandler and client this class is used to handle I/o via send/response commands with ExampleClient.
// Inofrmation arriving here can be taken out for business logic (NewBank) etc to handle banking operations.

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
                    break; // client disconnected
                }
                String response; // response is used for pairing in UI to handle UI case 1 or 2 (either login or menu UI presentation)

                ///////// Login with keyword to distinuish from menu command 
                if (request.startsWith("LOGIN")) {
                    String[] parts = request.split(" ");
                    if (parts.length < 3) {
                        response = "FAIL"; // at UI login entry both username and password should be entered 
                    } else {
                        String username = parts[1];
                        String password = parts[2];

                    // Authentication via newbank > PasswordManager, bank terminal out + user terminal response
                    customer = bank.checkLogInDetails(username, password);
                    if (customer != null) {
                        System.out.println("User Login: " + username);
                        response = "SUCCESS";
                    } else {
                        System.out.println("Failed user login: " + username);
                        response = "FAIL";
                    }
                }
            }

            // Log out handling, customer session end + logout repsonse
            else if (request.equals("LOGOUT")) {
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
            // send response back to client
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
