package newbank.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInterface {

    // fields
    private boolean isLoggedIn = false;
    private String username;
    private String password;
    private BufferedReader unserInput;
    
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
            if (isLoggedIn = false){
             // UI case 1
            }
            else {
             // UI case 2
            }
        }
}
  
  
  /*
  start program boolean logged in defval = false
  if loggedIn=false{
  System.out.print(
  "welcome message 
  enter username")
  
  user enter username
   store username in variable
   
  "enter password"
  
  user enter password
    
  start client
   send(username, password) via client to server
 }
   
  else if client recieves authentication success comf 
   
   switch boolean logged in to true
   
   display
   "login successfull 
   
   show msg + menu options"
   
   user input menu choice command
    switch 
     case SHOWMYACCOUNTS{send to client handler}
     case MOVEMONEY {send to client handler}
     case PAYMONEY {send to client handler}
     case CREATEACCOUNT {send to client handler}
     case SHOWACCOUNTDETAILS {send to client handler}
     case LOGOUT {return to start msg Case 1}
     
     case EXIT 
      are you sure you wsnt to (single loop case EXIT) {terminate socket, close program, clear terminal}
   

}