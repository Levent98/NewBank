package newbank.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ExampleClient {

    private Socket server;
    private PrintWriter bankServerOut;
    private BufferedReader serverIn;

<<<<<<< HEAD
    public ExampleClient(String ip, int port) throws UnknownHostException, IOException {
        server = new Socket(ip, port);
        bankServerOut = new PrintWriter(server.getOutputStream(), true);
        serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
=======
  public ExampleClient(String ip, int port) throws UnknownHostException, IOException {
    server = new Socket(ip,port);
    userInput = new BufferedReader(new InputStreamReader(System.in)); 
    // server.getOutputStream() gives you an output stream to the server
    // PrintWriter(server.getOutputStream(), true) wraps this stream to send lines of text (with auto-flush)
    bankServerOut = new PrintWriter(server.getOutputStream(), true); 

    bankServerResponseThread = new Thread() {
      private BufferedReader bankServerIn = new BufferedReader(new InputStreamReader(server.getInputStream())); 

      @Override
      public void run() {
        try {
          while(true) {
            String response = bankServerIn.readLine();
            // US01 Added to prevent a wall of "null" on the client
            if (response == null) {
              System.out.println("Disconnected from server, please reconnect or hit ctrl-D");
              break;
            }
            System.out.println(response);
          }
        } catch (IOException e) {
          e.printStackTrace();
          return;
        }
      }
    };
    bankServerResponseThread.start();
  }

  @Override
  public void run() {
    // restart the input loop if an exception occurs,
    // but usually, if userInput.readLine() fails, the program is not recoverable.
outer: // this is to modify the original code as little as possible but the while loops should be simplified
    while(true) {
      try {
        while(true) {
          String command = userInput.readLine();
          // US01 Added to be able to close the client with ctrl-D
          if (command == null) {
            System.out.println("Shutting down... bye!");
            break outer;
          }
          bankServerOut.println(command);
        }				
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        // no return stmt because the outer while restarts the thread if needed
      }
>>>>>>> origin/feature/UserStory01_SetPassword_TS
    }

    public void sendCommand(String command) {
        bankServerOut.println(command);
    }

    public String readResponse() throws IOException {
        String response = serverIn.readLine();

        if (response == null) {
            throw new IOException("Server disconnected");
        }

        return response;
    }

    // close server connection
    public void close() throws IOException {
        server.close();
    }
}