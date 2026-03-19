package newbank.server;

import java.util.HashMap;

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
  
  // Marking them synchronized forces those calls to run one-at-a-time on that single NewBank instance, which avoids certain race conditions.
  // They use customers which is a non thread safe hashmap
  public synchronized CustomerID checkLogInDetails(String userName, String password) {
    // US01 Added password check before returning the CustomerID
    if(customers.containsKey(userName) && passwords.check(userName, password)) {
      return new CustomerID(userName);
    }
    return null;
  }

  // commands from the NewBank customer are processed in this method
  public synchronized String processRequest(CustomerID customer, String request) {
    if(customers.containsKey(customer.getKey())) {
      switch(request) {
      case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
      default : return "Command not recognised.";
      }
    }
    return "FAIL";
  }
  
  private String showMyAccounts(CustomerID customer) {
    return (customers.get(customer.getKey())).accountsToString();
  }

}
