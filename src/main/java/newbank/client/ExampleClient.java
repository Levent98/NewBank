package newbank.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ExampleClient {

    // fields
    private Socket server;
    private PrintWriter serverOut;
    private BufferedReader serverIn;

    // constructor
    public ExampleClient(String ip, int port) throws IOException {
        server = new Socket(ip, port);
        serverOut = new PrintWriter(server.getOutputStream(), true);
        serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
    }

    // method sends command to server
    public void sendCommand(String command) {
        serverOut.println(command);
    }

    // method reads response from server
    public String readResponse() throws IOException {
        return serverIn.readLine();
    }

    // stop connection case method
    public void close() throws IOException {
        server.close();
    }
}