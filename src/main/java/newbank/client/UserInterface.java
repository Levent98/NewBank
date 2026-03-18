package newbank.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInterface {

    // fields
    private boolean isLoggedIn = false;
    private String username;
    private String password;
    private BufferedReader userInput;
    private ExampleClient client;
    
    // constructor
    public UserInterface(){
        userInput = new BufferedReader(new InputStreamReader(System.in)); 
    }
    // program start
     public static void main(String[] args){
        UserInterface ui = new UserInterface();
        ui.start();
    }
  
    // UI case 1 / 2 logic method
    public void start() {

        System.out.println("Welcome to NewBank");

        while (true) {
            if (isLoggedIn == false){
             // UI case 1
                try {
                    //ask for username
                    System.out.println("Enter Username ");
                    username =  userInput.readLine();
                    //ask for password
                    System.out.println("Enter Password ");
                    password = userInput.readLine();

                    //check login bool
                    if(client == null) {
                        client = new ExampleClient("localhost" ,14002);
                    }

                    // Send login 
                    client.sendCommand("LOGIN " + username + " " + password);
                    String response = client.readResponse(); 
                        if ("SUCCESS" .equals(response)) {
                            isLoggedIn = true;
                            System.out.println("Login Scuccessful\n");
                        } else {
                            System.out.println("Login Failed - Incorrect username or password\n");
                        }

                } catch (IOException e) {
                    System.out.println("An error has occured, please restart program\n");
                }    
            } else {
             // UI case 2 Menu display
                try {


                    System.out.print("Welcome to NewBank,\n");
                    System.out.print("This service is controlled via command line.\n");
                    System.out.print("Please type a command in the terminal window.\n");
                    
                   
                    System.out.println("\nMenu Options:");
                    System.out.println("\nSHOWMYACCOUNTS");
                    System.out.println("NEWACCOUNT <name>");
                    System.out.println("MOVE <amount> <from> <to>");
                    System.out.println("PAY <person> <amount>");
                    System.out.println("LOGOUT");
                    System.out.println("EXIT");

                    System.out.print("\nEnter command: ");
                    System.out.print("");
                    String userCommand = userInput.readLine();

                    // Log out locally (needs client side server connection cut + server side cancellation of customerID)

                    // send user command to server
                    client.sendCommand(userCommand);

                    // read and display response from server
                    String response = client.readResponse();
                    System.out.println("Server: " + response);

                } catch (IOException e) {
                    System.out.println("Error communicating with server");
                }
            }
        }    
    }
}  
  
  