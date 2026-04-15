package newbank.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class UserInterface implements ExampleClient.ResponseListener {

  private volatile boolean isLoggedIn = false;
  private boolean isNewUser = false;
  private volatile boolean isEmployee = false;
  private volatile boolean sessionTimedOut = false;
  private volatile boolean serverDisconnected = false;

  private String username;
  private String password;
  private final BufferedReader userInput;
  private ExampleClient client;
  private String response;

  private final BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();

  public UserInterface() {
    userInput = new BufferedReader(new InputStreamReader(System.in));
    startConsoleReaderThread();
  }

  public void start() {
    System.out.println("Welcome to NewBank");
    System.out.print("Are you a new user? Type \"YES\" or press Enter: ");

    try {
      response = takeInputLine();
    } catch (IOException e) {
      System.out.println("An error has occured, please restart program\n");
      return;
    }

    if ("YES".equals(response)) {
      System.out.println("We'll create you an account, please enter the new username and password");
      isNewUser = true;
    }

    while (true) {
      if (!isLoggedIn) {
        handleLoginFlow();
      } else {
        handlePostLoginFlow();
      }
    }
  }

  private void handleLoginFlow() {
    try {
      System.out.println("Enter Username ");
      username = takeInputLine().trim();

      System.out.println("Enter Password ");
      password = takeInputLine().trim();

      if (username.isEmpty() || username.contains("|") || password.isEmpty() || password.contains("|")) {
        System.out.println("Username and password must not be empty or contain '|'. Please try again.\n");
        return;
      }

      try {
        client = new ExampleClient("localhost", 14002);
        client.setResponseListener(this);
      } catch (IOException e) {
        System.out.println("Error: Unable to establish NewBank server connection - please try again");
        return;
      }

      try {
        client.sendCommand("LOGIN|" + username + "|" + password + "|" + (isNewUser ? "newUser" : ""));
        response = client.readResponse();

        if (response != null && response.startsWith("ERROR")) {
          System.out.println(response);
          closeClientQuietly();
          return;
        }

        isNewUser = false;

        if (response != null && response.startsWith("SUCCESS")) {
          isLoggedIn = true;
          sessionTimedOut = false;
          serverDisconnected = false;
          isEmployee = response.contains("EMPLOYEE");
          System.out.println("Login Successful\n");
          showMenu();
        } else {
          System.out.println("Login Failed - Incorrect username or password\n");
          closeClientQuietly();
        }
      } catch (IOException e) {
        System.out.println("Error: Server connection lost");
        closeClientQuietly();
      }

    } catch (IOException e) {
      System.out.println("An error has occured, please restart program\n");
    }
  }

  private void handlePostLoginFlow() {
    try {
      while (isLoggedIn && !sessionTimedOut && !serverDisconnected) {
        String userCommand = pollInputLine();

        if (sessionTimedOut || !isLoggedIn) {
          clearPendingInput();
          return;
        }

        if (userCommand == null) {
          continue;
        }

        userCommand = userCommand.trim();

        if ("EXIT".equals(userCommand)) {
          System.out.println("Thanks for using NewBank");
          if (client != null) {
            try {
              client.sendCommand("LOGOUT");
              client.readResponse();
            } catch (IOException e) {
              // ignore
            }
            closeClientQuietly();
          }
          System.exit(0);
        }

        if ("LOGOUT".equals(userCommand)) {
          client.sendCommand("LOGOUT");
          response = client.readResponse();
          System.out.println(response);
          clearSessionState();
          closeClientQuietly();
          return;
        }

        if (userCommand.isEmpty()) {
          System.out.print(isEmployee ? "EMPLOYEE> " : "CLIENT> ");
          continue;
        }

        if (userCommand.contains("|")) {
          System.out.println("\"|\" is not a valid character");
          System.out.print(isEmployee ? "EMPLOYEE> " : "CLIENT> ");
          continue;
        }

        String commandToSend = userCommand.replaceFirst(" ", "|");
        client.sendCommand(commandToSend);

        response = client.readResponse();
        String[] lines = response.split("\\|");

        System.out.println("NewBank: ");
        for (String item : lines) {
          System.out.println("  " + item.trim());
        }

        if ("LOGGED OUT".equals(response)) {
          clearSessionState();
          closeClientQuietly();
          return;
        }

        System.out.print(isEmployee ? "EMPLOYEE> " : "CLIENT> ");
      }
    } catch (IOException e) {
      System.out.println("Error communicating with server");
      clearSessionState();
      closeClientQuietly();
    }
  }

  private void startConsoleReaderThread() {
    Thread consoleReader = new Thread(() -> {
      try {
        while (true) {
          String line = userInput.readLine();
          if (line == null) {
            inputQueue.offer("__EOF__");
            break;
          }
          inputQueue.offer(line);
        }
      } catch (IOException e) {
        inputQueue.offer("__EOF__");
      }
    }, "newbank-console-reader");

    consoleReader.setDaemon(true);
    consoleReader.start();
  }

  private String takeInputLine() throws IOException {
    try {
      String line = inputQueue.take();
      if ("__EOF__".equals(line)) {
        throw new IOException("Console input closed");
      }
      return line;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Interrupted while waiting for console input", e);
    }
  }

  private String pollInputLine() throws IOException {
    try {
      String line = inputQueue.poll(200, TimeUnit.MILLISECONDS);
      if (line == null) {
        return null;
      }
      if ("__EOF__".equals(line)) {
        throw new IOException("Console input closed");
      }
      return line;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Interrupted while waiting for console input", e);
    }
  }

  private void clearPendingInput() {
    inputQueue.clear();
  }

  private void clearSessionState() {
    isLoggedIn = false;
    isEmployee = false;
    sessionTimedOut = false;
    serverDisconnected = false;
  }

  private void closeClientQuietly() {
    if (client != null) {
      try {
        client.close();
      } catch (IOException e) {
        // ignore
      } finally {
        client = null;
      }
    }
  }

  private void showMenu() {
    System.out.print("Welcome to NewBank,\n");
    System.out.print("This service is controlled via command line.\n");
    System.out.print("Please type a command in the terminal window.\n");

    System.out.println("\nMenu Options:");
    if (isEmployee) {
      System.out.println("\nVIEWALL");
      System.out.println("VIEWTRANSACTIONS");
      System.out.println("TESTADDMONEY <toUsername> <toAccount> <amount> <source>");
    } else {
      System.out.println("\nSHOWMYACCOUNTS");
      System.out.println("NEWACCOUNT <name>");
      System.out.println("MOVE <amount> <from> <to>");
      System.out.println("PAY <fromaccount> <payee> <amount>");
    }
    System.out.println("LOGOUT");
    System.out.println("EXIT");
    System.out.println("CHANGEPW <new password>");

    System.out.println("\nEnter command at the prompt");
    System.out.print(isEmployee ? "EMPLOYEE> " : "CLIENT> ");
  }

  @Override
  public void onTimeout() {
    sessionTimedOut = true;
    isLoggedIn = false;
    isEmployee = false;

    synchronized (System.out) {
      System.out.println();
      System.out.println("NewBank: ");
      System.out.println("  LOGGED OUT - Session timed out");
      System.out.println();
    }

    closeClientQuietly();
    clearPendingInput();
  }

  @Override
  public void onDisconnected() {
    serverDisconnected = true;
    isLoggedIn = false;
    isEmployee = false;

    synchronized (System.out) {
      System.out.println();
      System.out.println("Server disconnected.");
    }

    closeClientQuietly();
    clearPendingInput();
  }

  public static void main(String[] args) {
    UserInterface ui = new UserInterface();
    ui.start();

   
  } 
}  


