package newbank.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInterface {

  private boolean isLoggedIn = false;
  private boolean isNewUser = false;
  private boolean isEmployee = false;
  private String username;
  private String password;
  private BufferedReader userInput;
  private ExampleClient client;
  private String response;

  public UserInterface() {
    userInput = new BufferedReader(new InputStreamReader(System.in));
  }

  public void start() {
    System.out.println("Welcome to NewBank");

    while (true) {
      if (!isLoggedIn) {
        // --- CASE 1: PRE-LOGIN SEQUENCE ---
        System.out.print("Are you a new user? Type \"YES\" or press Enter: ");
        try {
          response = userInput.readLine();
          if ("YES".equalsIgnoreCase(response)) {
            System.out.println("We'll create you an account, please enter the new username and password");
            isNewUser = true;
          }

          System.out.println("Enter Username ");
          username = userInput.readLine().trim();
          System.out.println("Enter Password ");
          password = userInput.readLine().trim();

          if (username.isEmpty() || username.contains("|") || password.isEmpty() || password.contains("|")) {
            System.out.println("Username and password must not be empty or contain '|'. Please try again.\n");
            continue;
          }

          try {
            client = new ExampleClient("localhost", 80);
            client.sendCommand("LOGIN|" + username + "|" + password + "|" + (isNewUser ? "newUser" : ""));
            response = client.readResponse();

            if (response != null && response.startsWith("ERROR")) {
              System.out.println(response);
              isNewUser = false;
              continue;
            }

            if (response != null && response.startsWith("SUCCESS")) {
              isLoggedIn = true;
              isNewUser = false;
              isEmployee = response.contains("EMPLOYEE");
              System.out.println("Login Successful\n");
              showMenu();
            } else {
              System.out.println("Login Failed - Incorrect username or password\n");
            }
          } catch (IOException e) {
            System.out.println("Error: Unable to establish NewBank server connection - please try again");
          }

        } catch (IOException e) {
          System.out.println("An error has occured, please restart program\n");
        }
      } else {
        // --- CASE 2: POST-LOGIN COMMAND HANDLING ---
        try {
          String userCommand = userInput.readLine();

          if (userCommand == null || "EXIT".equalsIgnoreCase(userCommand)) {
            System.out.println("Thanks for using NewBank");
            if (client != null) {
              client.sendCommand("LOGOUT");
              client.close();
            }
            break;
          }

          if ("LOGOUT".equalsIgnoreCase(userCommand)) {
            client.sendCommand("LOGOUT");
            response = client.readResponse();
            System.out.println(response);
            resetSession();
            continue;
          } else if (userCommand.contains("|")) {
            System.out.println("\"|\" is not a valid character");
            continue;
          }

          String commandToSend = userCommand.replaceFirst(" ", "|");
          client.sendCommand(commandToSend);
          response = client.readResponse();

          // Check if response contains the timeout message
          if (response == null || response.equals("LOGGED OUT - Session timed out")) {
            displayTimeoutMessage();
            resetSession();
            continue;
          }

          String[] lines = response.split("\\|");
          System.out.println("NewBank: ");
          for (String item : lines) {
            System.out.println("  " + item.trim());
          }
          System.out.print(isEmployee ? "EMPLOYEE> " : "CLIENT> ");

        } catch (IOException e) {
          // If the socket was closed by the server (timeout), this catch block handles it
          displayTimeoutMessage();
          resetSession();
        }
      }
    }
  }

  // Helper method to show the UI requirements box
  private void displayTimeoutMessage() {
    System.out.println("\n##########################################");
    System.out.println("#                                        #");
    System.out.println("#      LOGGED OUT - Session timed out    #");
    System.out.println("#    Returning to login sequence...      #");
    System.out.println("#                                        #");
    System.out.println("##########################################\n");
  }

  // Helper method to clear flags and close client
  private void resetSession() {
    isLoggedIn = false;
    isEmployee = false;
    if (client != null) {
      try { client.close(); } catch (IOException ignored) {}
    }
  }

  private void showMenu() {
    System.out.print("Welcome to NewBank,\n");
    System.out.print("This service is controlled via command line.\n");
    System.out.print("Please type a command in the terminal window.\n");

    System.out.println("\nMenu Options:");
    if (isEmployee) {
      System.out.println("VIEWALL");
    } else {
      System.out.println("SHOWMYACCOUNTS");
      System.out.println("NEWACCOUNT <name>");
      System.out.println("MOVE <amount> <from> <to>");
      System.out.println("PAY <person> <amount>");
    }
    System.out.println("CHANGEPW <new password>");
    System.out.println("LOGOUT");
    System.out.println("EXIT");

    System.out.println("\nEnter command at the prompt");
    System.out.print(isEmployee ? "EMPLOYEE> " : "CLIENT> ");
  }

  public static void main(String[] args) {
    UserInterface ui = new UserInterface();
    ui.start();
  }
}