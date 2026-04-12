package newbank.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewBankServer extends Thread {

  private ServerSocket server;

  private final ExecutorService threadPool = Executors.newFixedThreadPool(10);

  public NewBankServer(int port) throws IOException {
    server = new ServerSocket(port);
  }

  @Override
  public void run() {
    System.out.println("New Bank Server listening on " + server.getLocalPort());
    try {
      while(true) {
        Socket s = server.accept();

        threadPool.execute(new NewBankClientHandler(s));
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      shutdownServer();
    }
  }

  private void shutdownServer() {
    try {
      if (server != null) server.close();
      threadPool.shutdown();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException {

    int port = (args.length > 0) ? Integer.parseInt(args[0]) : 14002;
    new NewBankServer(port).start();
  }
}