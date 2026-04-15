package newbank.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ExampleClient {

    public interface ResponseListener {
        void onTimeout();
        void onDisconnected();
    }

    private final Socket server;
    private final PrintWriter bankServerOut;
    private final BufferedReader serverIn;

    private final BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;
    private volatile ResponseListener listener;
    private final Thread listenerThread;

    public ExampleClient(String ip, int port) throws UnknownHostException, IOException {
        server = new Socket(ip, port);
        bankServerOut = new PrintWriter(server.getOutputStream(), true);
        serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));

        listenerThread = new Thread(this::listenToServer, "newbank-server-listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void setResponseListener(ResponseListener listener) {
        this.listener = listener;
    }

    public void sendCommand(String command) {
        bankServerOut.println(command);
    }

    public String readResponse() throws IOException {
        try {
            String response = responseQueue.take();

            if ("__SERVER_DISCONNECTED__".equals(response)) {
                throw new IOException("Server disconnected");
            }

            return response.trim();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for server response", e);
        }
    }

    private void listenToServer() {
        try {
            while (running) {
                String response = serverIn.readLine();

                if (response == null) {
                    running = false;
                    responseQueue.offer("__SERVER_DISCONNECTED__");
                    if (listener != null) {
                        listener.onDisconnected();
                    }
                    break;
                }

                response = response.trim();

                if ("LOGGED OUT - Session timed out".equals(response)) {
                    if (listener != null) {
                        listener.onTimeout();
                    }
                } else {
                    responseQueue.offer(response);
                }
            }
        } catch (IOException e) {
            if (running) {
                responseQueue.offer("__SERVER_DISCONNECTED__");
                if (listener != null) {
                    listener.onDisconnected();
                }
            }
        }
    }

    public void close() throws IOException {
        running = false;
        server.close();
    }
}