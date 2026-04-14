package newbank.server;

import java.util.ArrayList;
import java.util.HashMap;

public class NewBank {

  private static final NewBank bank = new NewBank();
  private final HashMap<String, Customer> customers;
  private final PasswordManager customerPasswords;
  private final PasswordManager employeePasswords;

  private NewBank() {
    customers = new HashMap<>();
    customerPasswords = new PasswordManager();
    employeePasswords = new PasswordManager();
    addTestData();
  }

  public void reset() {
    customers.clear();
    customerPasswords.clear();
    employeePasswords.clear();
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

    Customer newCustomer = new Customer();
    
    customers.put(userName, newCustomer);
    return "SUCCESS: Account created";
  }

  public synchronized CustomerID checkLogInDetails(String userName, String password) {
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
    if (customerPasswords.hasUserName(userName)) {
      return customerPasswords.set(userName, password);
    }
    if (employeePasswords.hasUserName(userName)) {
      return employeePasswords.set(userName, password);
    }
    return "FAIL";
  }

  public synchronized CustomerID setLogInDetails(String userName, String password) {
    if (userName == null || customers.containsKey(userName)) {
      return null;
    }
    Customer user = new Customer();
  
    customers.put(userName, user);
    customerPasswords.set(userName, password);
    return new CustomerID(userName);
  }

  public synchronized String processRequest(CustomerID customer, String command, String args) {
    if (customer == null) {
      return "Command not recognised";
    }

    boolean isEmployee = customer.isEmployee();
    boolean isCustomer = customers.containsKey(customer.getKey());

    if (!isEmployee && !isCustomer) {
      return "Command not recognised";
    }

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
    }
  }

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
    return customers.get(customer.getKey()).accountsToString();
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
        ? "SUCCESS - a new account '" + accountName + "' has been created. Minimum opening deposit of £1 required before activation."
        : "FAIL - an error occured.";
  }

  public String moveMoney(CustomerID customerID, String args) {
    Customer customer = customers.get(customerID.getKey());

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

    TransactionManager transactionManager;
    try {
      transactionManager = new TransactionManager(customer, customers, "PAY " + args);
    } catch (Exception e) {
      return e.getMessage();
    }
    return transactionManager.payMoney();
  }

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
      return "FAILURE - " + args.trim() + " has negative balance";
    }

    String balanceTransferMessage = "";
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

    accounts.remove(i);
    customer.getDeactivatedAccounts().add(account);
    return "SUCCESS - " + args.trim() + " deactivated." + balanceTransferMessage;
  }
}