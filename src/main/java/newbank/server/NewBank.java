package newbank.server;

<<<<<<< HEAD
=======
import java.io.BufferedReader;
import java.io.StringReader;
>>>>>>> develop
import java.util.ArrayList;
import java.util.HashMap;

public class NewBank {

  private static final NewBank bank = new NewBank();
  // HashMap is not thread-safe with concurrent writes (adding/removing customers/accounts, transfers, etc.),
  // unsynchronized access can lead to: lost updates inconsistent reads
  private final HashMap<String, Customer> customers;
  // US01 Added a PasswordManager object to store passwords
  private final PasswordManager customerPasswords;
  // US09 Added an employee instance
  private final PasswordManager employeePasswords;

  private NewBank() {
    customers = new HashMap<>();
    // US01 and 09 Added a PasswordManager object to store passwords
    customerPasswords = new PasswordManager();
    employeePasswords = new PasswordManager();
    addTestData();
  }

  public void reset() {
    customers.clear();
<<<<<<< HEAD
    customerPasswords.clear();
    employeePasswords.clear();
=======
>>>>>>> develop
    addTestData();
  }

  private void addTestData() {
    Customer bhagy = new Customer();
    bhagy.addAccount(new Account("Main", 1000.0f));
    bhagy.addAccount(new Account("Credit Card", -100.0f));
    bhagy.addAccount(new Account("Main2", 1000.0f));
    bhagy.addAccount(new Account("Main3", 1000.0f));
    customers.put("Bhagy", bhagy);
    customerPasswords.setUnchecked("Bhagy", "bhagy");

    Customer christina = new Customer();
    christina.addAccount(new Account("Savings", 1500.0f));
    customers.put("Christina", christina);
    customerPasswords.setUnchecked("Christina", "christina");

    Customer john = new Customer();
    john.addAccount(new Account("Checking", 250.0f));
    customers.put("John", john);
    customerPasswords.setUnchecked("John", "john");

    employeePasswords.setUnchecked("Admin", "admin");
  }

  public static NewBank getBank() {
    return bank;
  }

  public synchronized String createLogInDetails(String userName, String password) {
    if (customers.containsKey(userName)) {
      return "ERROR: Username already in use";
    }
    String response = customerPasswords.set(userName, password);
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
    if (customers.containsKey(userName) && customerPasswords.check(userName, password)) {
      return new CustomerID(userName, CustomerID.Role.CUSTOMER);
    }
    if (employeePasswords.check(userName, password)) {
      return new CustomerID(userName, CustomerID.Role.EMPLOYEE);
    }
    return null;
  }

  public String changeLogInPassword(String userName, String password) {
    if (password == null) {
      return "FAIL";
    }
    // Update whichever store owns this username
    if (customerPasswords.hasUserName(userName)) {
      return customerPasswords.set(userName, password);
    }
    if (employeePasswords.hasUserName(userName)) {
      return employeePasswords.set(userName, password);
    }
    return "FAIL";
  }

  // US01 simple account creation following addTestData() after checking for account already in use
  public synchronized CustomerID setLogInDetails(String userName, String password) {
    if (userName == null || customers.containsKey(userName)) {
      return null;
    }
    Customer user = new Customer();
    user.addAccount(new Account("Checking", 250.0f));
    customers.put(userName, user);
    customerPasswords.set(userName, password);
    return new CustomerID(userName);
  }

  // commands from the NewBank customer are processed in this method
  public synchronized String processRequest(CustomerID customer, String command, String args) {
    if (customer == null) {
      return "Command not recognised";
    }

    // this could be done vs password manager if tests are changed
    boolean isEmployee = customer.isEmployee();
    boolean isCustomer = customers.containsKey(customer.getKey());

    // Employees are always admitted; non-employees must be in the customers map
    if (!isEmployee && !isCustomer) {
      return "Command not recognised";
    }

    // CLI action based on user input - this is where we could add further commands
    switch (command) {
      case "VIEWALL":
        if (!isEmployee) return "Command not recognised";
        return viewAllCustomers();
      case "SHOWMYACCOUNTS":
        if (!isCustomer) return "Command not recognised";
        return showMyAccounts(customer);
      case "NEWACCOUNT":
        if (!isCustomer) return "Command not recognised";
        return handleNewAccount(customer, args);
      case "MOVE":
        if (!isCustomer) return "Command not recognised";
        return moveMoney(customer, args);
      case "PAY":
        if (!isCustomer) return "Command not recognised";
        return payMoney(customer, args);
      case "DEACTIVATE":
        if (!isCustomer) return "Command not recognised";
        return deactivateAccount(customer, args);
      case "TESTADDMONEY":
        if (!isEmployee) return "Command not recognised";
        return testAddMoney(args);
      default:
        return "Command not recognised";
<<<<<<< HEAD
=======
      }

      // this could be done vs password manager if tests are changed
      boolean isEmployee = customer.isEmployee();
      boolean isCustomer = customers.containsKey(customer.getKey());

      // Employees are always admitted; non-employees must be in the customers map
      if (!isEmployee && !isCustomer) {
        return "Command not recognised";
      }

    // CLI action based on user input - this is where we could add further commands
    switch (command) {
      case "VIEWALL":
        if (!isEmployee) return "Command not recognised";
        return viewAllCustomers();
      case "SHOWMYACCOUNTS":
        if (!isCustomer) return "Command not recognised";
        return showMyAccounts(customer);
      case "NEWACCOUNT":
        if (!isCustomer) return "Command not recognised";
        return handleNewAccount(customer, args);
      case "MOVE":
        if (!isCustomer) return "Command not recognised";
        return moveMoney(customer, args);
      case "PAY":
        if (!isCustomer) return "Command not recognised";
        return payMoney(customer, args);
      case "DEACTIVATE":
        if(!isCustomer) return "Command not recognised";
        return deactivateAccount(customer, args);
      default:
        return "Command not recognised";
>>>>>>> develop
    }
  }

  // VIEWALL customer string builder method
  // Creates a mutable string object to build a final output that can be updated upon each use without creating multiple string objects and consuming memory/CPU before garbage collection occurs
  private String viewAllCustomers() {
    StringBuilder result = new StringBuilder();
    for (String customerName : customers.keySet()) {
      Customer customer = customers.get(customerName);
      result.append(customerName)
          .append(": ")
          .append("|")
          .append(customer.accountsToString())
          .append("|");
    }
    return result.toString();
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

  // Confirms account has been made or if account has not been made. Fail only happens now if >10 accounts created
  private String newAccount(CustomerID customerID, String accountName) {
    Customer c = customers.get(customerID.getKey());

    boolean success = c.addAccount(accountName);

    return success
        ? "SUCCESS - a new account '" + accountName + "' has been created."
        : "FAIL - an error occured.";
  }

  public String moveMoney(CustomerID customerID, String args) {
    Customer customer = customers.get(customerID.getKey());

    // Here we need to pass the values to the TransactionManager
    TransactionManager transactionManager;
    try {
      transactionManager = new TransactionManager(customer, "MOVE " + args);
    } catch (Exception e) {
      return e.getMessage();
    }
    return transactionManager.moveMoney();
  }

  public String payMoney(CustomerID customerID, String args) {
    Customer customer = customers.get(customerID.getKey());

    // Here we need to pass the values to the TransactionManager
    TransactionManager transactionManager;
    try {
      transactionManager = new TransactionManager(customer, customers, "PAY " + args);
    } catch (Exception e) {
      return e.getMessage();
    }
    return transactionManager.payMoney();
  }

<<<<<<< HEAD
  public String testAddMoney(String args) {
    String[] parts = args.split(" ", 4);

    if (parts.length < 4) {
      return "ERROR: TESTADDMONEY command must be in the format \"TESTADDMONEY CUSTOMER TOACCOUNT AMOUNT SOURCE\"";
    }

    String customerName = parts[0];
    String accountName = parts[1];

    float amount;
    try {
      amount = Float.parseFloat(parts[2]);
    } catch (NumberFormatException e) {
      return "ERROR: Value is in the incorrect format.";
    }

    String source = parts[3];

    return processAddMoney(new CustomerID(customerName), accountName, amount, source);
  }

  public String processAddMoney(CustomerID customerID, String accountName, float amount, String source) {
    if (!customers.containsKey(customerID.getKey())) {
      return "FAIL - Customer name not valid";
    }

    Customer customer = customers.get(customerID.getKey());

    TransactionManager transactionManager;
    try {
      transactionManager = new TransactionManager(
          customer,
          "ADDMONEY " + accountName + " " + String.format("%.2f", amount) + " " + source
      );
    } catch (Exception e) {
      return e.getMessage();
    }

    return transactionManager.addMoney();
  }

  private String deactivateAccount(CustomerID customerID, String args) {
    if (args == null || args.isEmpty()) {
      return "FAILURE - Provide account to deactivate";
    }

    Customer customer = customers.get(customerID.getKey());
    ArrayList<Account> accounts = customer.getAccounts();
    Account account = null;
    int i;
    for (i = 0; i < accounts.size(); i++) {
      account = accounts.get(i);
      if (account.getName().equals(args.trim())) {
        break;
      }
    }

    if (i == accounts.size()) {
      return "FAILURE - " + args.trim() + " does not exist";
    }

    if (account.getBalance() < 0) {
=======
  private String deactivateAccount(CustomerID customerID, String args) {
    if(args.isEmpty() || args==null){
      return "FAILURE - Provide account to deactivate";
    }

    // Get customer
    Customer customer = customers.get(customerID.getKey());
    // Get account
    // getAccounts() used instead of getAccount() in order to find index of account
    ArrayList<Account> accounts = customer.getAccounts();
    Account account = null;
    int i;
    for(i = 0; i<accounts.size(); i++){
      account = accounts.get(i);
      if(account.getName().equals(args.trim())) {
        break;
      }
    }
    // Return failure string if account does not exist
    if (i==accounts.size()){
      return "FAILURE - " + args.trim() + " does not exist";
    }

    // Check that the account does not have negative balance
    if(account.getBalance()<0){
>>>>>>> develop
      return "FAILURE - " + args.trim() + " has negative balance";
    }

    String balanceTransferMessage = "";
<<<<<<< HEAD
    if (account.getBalance() > 0) {
      if (accounts.size() == 1) {
        return "FAILURE - " + args.trim() + " is the only active account, with a balance of "
            + account.getBalance() + ". Balance must be 0.";
      }
      if (i == 0) {
        accounts.get(1).deposit(account.getBalance(), "Move from " + args.trim());
        balanceTransferMessage = " Remaining balance of " + account.getBalance()
            + " moved to " + accounts.get(1).getName();
      } else {
        accounts.get(0).deposit(account.getBalance(), "Move from " + args.trim());
        balanceTransferMessage = " Remaining balance of " + account.getBalance()
            + " moved to " + accounts.get(0).getName();
      }
    }

=======
    // Check if the account has balance to be transferred to next primary account
    if(account.getBalance()>0){
      // Return failure string if account is the only one
      if(accounts.size()==1){
        return "FAILURE - " + args.trim() + " is the only active account, with a balance of " + account.getBalance() + ". Balance must be 0.";
      }
      // If the first account transfer to second, else first
      if(i==0){
        accounts.get(1).deposit(account.getBalance(),"Move from " + args.trim());
        balanceTransferMessage = " Remaining balance of " + account.getBalance() + " moved to " + accounts.get(1).getName();
      }
      else{
        accounts.get(0).deposit(account.getBalance(),"Move from " + args.trim());
        balanceTransferMessage = " Remaining balance of " + account.getBalance() + " moved to " + accounts.get(0).getName();
      }
    }
    // remove account from accounts and add to deactivatedAccounts
>>>>>>> develop
    accounts.remove(i);
    customer.getDeactivatedAccounts().add(account);
    return "SUCCESS - " + args.trim() + " deactivated." + balanceTransferMessage;
  }
}