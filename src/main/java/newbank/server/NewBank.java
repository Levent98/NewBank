package newbank.server;

import java.util.HashMap;

public class NewBank {
  
  private static final NewBank bank = new NewBank();
  private HashMap<String,Customer> customers;
  
  private NewBank() {
    customers = new HashMap<>();
    addTestData();
  }
  
  private void addTestData() {
    Customer bhagy = new Customer();
    bhagy.addAccount(new Account("Main", 1000.0));
    customers.put("Bhagy", bhagy);
    
    Customer christina = new Customer();
    christina.addAccount(new Account("Savings", 1500.0));
    customers.put("Christina", christina);
    
    Customer john = new Customer();
    john.addAccount(new Account("Checking", 250.0));
    customers.put("John", john);
  }
  
  public static NewBank getBank() {
    return bank;
  }
  
  public synchronized CustomerID checkLogInDetails(String userName, String password) {
    if(customers.containsKey(userName)) {
      return new CustomerID(userName);
    }
    return null;
  }

  // commands from the NewBank customer are processed in this method
  public synchronized String processRequest(CustomerID customer, String request) {
    if(customers.containsKey(customer.getKey())) {

      // Find the first space
      int firstSpace = request.indexOf(" ");

      // Extract command
      String command = (firstSpace == -1)
        ? request
        : request.substring(0, firstSpace);

      // Extract argument (account name)
      String argument = (firstSpace == -1)
        ? null
        : request.substring(firstSpace + 1).trim();

      switch(command) {
      case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
      

      case "NEWACCOUNT": return handleNewAccount(customer,argument);
      default : return "FAIL";
      }
    }
    return "FAIL";
  }
  
  private String showMyAccounts(CustomerID customer) {
    return (customers.get(customer.getKey())).accountsToString();
  }
  private String handleNewAccount(CustomerID customer, String accountName) {
    if (accountName == null || accountName.isEmpty()) {
      return "You must specify an account name.";
    }

    return newAccount(customer, accountName);
  }

  private String newAccount(CustomerID customerID, String accountName) {
    Customer c = customers.get(customerID.getKey());

    boolean success = c.addAccount(accountName);

    return success
        ? "SUCCESS - a new account '" + accountName + "' has been created."
        : "FAIL - an error occured.";
  }



}
