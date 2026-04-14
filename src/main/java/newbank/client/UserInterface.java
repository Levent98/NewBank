// UI class handles user input and display messages back to terminal.

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
  // Avoiding declaring response at every input
  private String response;

  // constructor starts wrapped input stream
  public UserInterface() {
    userInput = new BufferedReader(new InputStreamReader(System.in)); 
  }

  // UI case 1 Or 2 logic method
  public void start() {

    System.out.println("Welcome to NewBank");

    // User creation, server will by default create a new user for a LOGIN command on non existent customer if flag newUser is passed
    System.out.print("Are you a new user? Type \"YES\" or press Enter: ");
    try {
      response = userInput.readLine();
    } catch (IOException e) {
      System.out.println("An error has occured, please restart program\n");
    }
    if ("YES".equals(response)) {
      System.out.println("We'll create you an account, please enter the new username and password");
      isNewUser = true;
    }

    while (true) {
      if (isLoggedIn == false){
        // UI case 1 Pre login
        try {
          //ask for username
          System.out.println("Enter Username ");
          username =  userInput.readLine().trim();
          //ask for password
          System.out.println("Enter Password ");
          password = userInput.readLine().trim();

          // Input validation "|" will be used for messages
          if (username.isEmpty() || username.contains("|") || password.isEmpty() || password.contains("|")) {
            System.out.println("Username and password must not be empty or contain '|'. Please try again.\n");
            continue;
          }

          //start new client connection
          try {
            client = new ExampleClient("localhost" ,14002); // ExmapleCLient object establishes server conection
          } catch (IOException e) {
            System.out.println("Error: Unable to establish NewBank server connection - please try again");
            continue;
          }

          try {
            // sends login details with LOGIN keyword so ClientHandler knows to authenticate + flag for user creation
            // changed " " to "|" to separate arguments in the message sent to the server
            client.sendCommand("LOGIN|" + username + "|" + password + "|" + (isNewUser ? "newUser" : ""));
            response = client.readResponse(); 

            // Need more complex error messages to account for weak password and already existing username
            if (response != null && response.startsWith("ERROR")) {
              System.out.println(response);
              continue;
            }
            isNewUser = false; // reset isNewUser to false

            if (response != null && response.startsWith("SUCCESS")) {
              isLoggedIn = true;
              // US09 to give visual feedback to the UI
              isEmployee = response.contains("EMPLOYEE");
              System.out.println("Login Successful\n");
              showMenu(); // display command menu
            } else {
              System.out.println("Login Failed - Incorrect username or password\n");
            }
          } catch (IOException e) {
            System.out.println("Error: Server connection lost");
            client = null; // reset client
          }

        } catch (IOException e) {
          System.out.println("An error has occured, please restart program\n");
        }    
      } else {
        // UI case 2 Post login command handling
        try {
          String userCommand = userInput.readLine();

          // Exit program locally (needs to trigger client side server connection cut + server side cancellation of customerID)
          if ("EXIT".equals(userCommand)){
            System.out.println("Thanks for using NewBank");
            if(client != null) {
              client.sendCommand("LOGOUT");
              client.readResponse();
              client.close();
            }
            break; // stop
          }
          // Lougout locally (needs to trigger switch to UI login state + server side cancellation of customerID)
          if ("LOGOUT".equals(userCommand)){
            client.sendCommand("LOGOUT");
            response = client.readResponse();
            System.out.println(response);
            // Clearing flags here
            isLoggedIn = false;
            isEmployee = false;
            continue; // back to login
          } else if (userCommand.contains("|")) {
            System.out.println("\"|\" is not a valid character");
            continue;
          }

          // send user command to server, switching to new protocole: command|arguments
          String commandToSend = userCommand.replaceFirst(" ", "|");
          client.sendCommand(commandToSend);

          // read and display response from server
          response = client.readResponse();
          String[] lines = response.split("\\|");
          // process multi line reponses
          System.out.println("NewBank: ");
          for (String item : lines) {
            System.out.println("  " + item.trim());
          }
          System.out.print(isEmployee ? "EMPLOYEE> " : "CLIENT> ");

        } catch (IOException e) {
          System.out.println("Error communicating with server");
        }
      }
    }    
  }
  // menu display method
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
  // Client side program start
  public static void main(String[] args) {
    UserInterface ui = new UserInterface();
    ui.start();
  } 
}  

