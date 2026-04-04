package newbank.server;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.StringTokenizer;

public class NewBank {

  private static final NewBank bank = new NewBank();
  // HashMap is not thread-safe with concurrent writes (adding/removing customers/accounts, transfers, etc.),
  // unsynchronized access can lead to: lost updates inconsistent reads
  private HashMap<String,Customer> customers;
  // US01 Added a PasswordManager object to store passwords
  private PasswordManager passwords;
  
  private NewBank() {
    customers = new HashMap<>();
    addTestData();
    // US01 Added a PasswordManager object to store passwords
    passwords = PasswordManager.getPasswordManager();
  }

  private void addTestData() {
    Customer bhagy = new Customer();
    bhagy.addAccount(new Account("Main", 1000.0f));
    customers.put("Bhagy", bhagy);

    Customer christina = new Customer();
    christina.addAccount(new Account("Savings", 1500.0f));
    customers.put("Christina", christina);

    Customer john = new Customer();
    john.addAccount(new Account("Checking", 250.0f));
    customers.put("John", john);
  }

  public static NewBank getBank() {
    return bank;
  }

  public synchronized String createLogInDetails(String userName, String password) {
    if (customers.containsKey(userName)) {
      return "ERROR: Username already in use";
    }
    String response = passwords.set(userName, password);
    if (response.startsWith("ERROR")) {
      return response;
    }

    // TODO as we won't give people money but copied from addTestData for now
    Customer newCustomer = new Customer();
    newCustomer.addAccount(new Account("Checking", 250.0f));
    customers.put(userName, newCustomer);
    return "SUCCESS: Account created";
  }
  
  // Marking them synchronized forces those calls to run one-at-a-time on that single NewBank instance, which avoids certain race conditions.
  // They use customers which is a non thread safe hashmap
  public synchronized CustomerID checkLogInDetails(String userName, String password) {
    // US02 Added password check before returning the CustomerID
    if(customers.containsKey(userName) && passwords.check(userName, password)) {
      return new CustomerID(userName);
    }
    return null;
  }

  public String changeLogInPassword(String userName, String password) {
    if(!customers.containsKey(userName) || password == null) {
      return "FAIL";
    }
    return passwords.set(userName, password);
  }

  // US01 simple account creation following addTestData() after checking for account already in use
  public synchronized CustomerID setLogInDetails(String userName, String password) {
    if (userName == null || customers.containsKey(userName)) {
      return null;
    }
    Customer user = new Customer();
    user.addAccount(new Account("Checking", 250.0f));
    customers.put(userName, user);
    passwords.set(userName, password);
    return new CustomerID(userName);
  }

  // commands from the NewBank customer are processed in this method
  public synchronized String processRequest(CustomerID customer, String request) {
    //protect customer ID stauts against customer = null in client handler
    if(customer != null && customers.containsKey(customer.getKey())) {
  
      // Find the first space as expects user to type in "NEWACCOUNT ACCOUNTNAME"
      int firstSpace = request.indexOf(" ");

      // Extract command
      String command = (firstSpace == -1)
              ? request
              : request.substring(0, firstSpace);

      // Extract argument (account name)
      String argument = (firstSpace == -1)
              ? null
              : request.substring(firstSpace + 1).trim();

      // CLI action based on user input - this is where we could add further commands
      switch (command) {
        case "SHOWMYACCOUNTS":
          return showMyAccounts(customer);
        case "NEWACCOUNT":
          return handleNewAccount(customer, argument);
        case "MOVE":
          return moveMoney(customer, request);
        default:
          return "Command not recognised";
      }
    }
    return "Command not recognised";
  }

  private String showMyAccounts(CustomerID customer) {
    return (customers.get(customer.getKey())).accountsToString();
  }

  // If there is no account name given by the user after NEWACCOUNT the program returns the message
  private String handleNewAccount(CustomerID customer, String accountName) {
    if (accountName == null || accountName.isEmpty()) {
      return "You must specify an account name.";
    }
    return newAccount(customer, accountName);
  }

  // Confirms account has been made or if account has not been made.  Fail only happens now if >10 accounts created
  private String newAccount(CustomerID customerID, String accountName) {
    Customer c = customers.get(customerID.getKey());

    boolean success = c.addAccount(accountName);

    return success
            ? "SUCCESS - a new account '" + accountName + "' has been created."
            : "FAIL - an error occured.";
  }

  public String moveMoney(CustomerID customerID, String request) {
    Customer customer = customers.get(customerID.getKey());

    // Here we need to pass the values to the TransactionManager
    TransactionManager transactionManager;
    try {
      transactionManager = new TransactionManager(customer, request);
    } catch (Exception e) {
      return e.getMessage();
    }
    return transactionManager.moveMoney();
  }
}