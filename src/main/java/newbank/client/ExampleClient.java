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

    public ExampleClient(String ip, int port) throws UnknownHostException, IOException {
        server = new Socket(ip, port);
        bankServerOut = new PrintWriter(server.getOutputStream(), true);
        serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));   
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