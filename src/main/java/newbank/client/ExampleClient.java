package newbank.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ExampleClient extends Thread{

  private Socket server;
  private PrintWriter bankServerOut;	
  private BufferedReader userInput;
  private Thread bankServerResponseThread;

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
            String responce = bankServerIn.readLine();
            System.out.println(responce);
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
    while(true) {
      try {
        while(true) {
          String command = userInput.readLine();
          bankServerOut.println(command);
        }				
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        // no return stmt because the outter while restarts the thread if needed
      }
    }
  }

  public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
    new ExampleClient("localhost",14002).start();
  }
}
