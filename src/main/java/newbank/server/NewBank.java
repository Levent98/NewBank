package newbank.server;

import java.util.HashMap;

public class NewBank {

  private static NewBank bank = null;
  private HashMap<String, Customer> customers;
  private PasswordManager passwordManager;

  private NewBank() {
    customers = new HashMap<>();
    this.passwordManager = new PasswordManager();
    addTestData();
  }

  public static NewBank getBank() {
    if (bank == null) {
      bank = new NewBank();
    }
    return bank;
  }

  private void addTestData() {
    customers.put("Bhagy", new Customer("Bhagy"));
    customers.put("John", new Customer("John"));
  }


  public synchronized String handleLogin(String username, String password, boolean isNewUser) {
    if (isNewUser) {
      String result = passwordManager.set(username, password);
      if (result.startsWith("SUCCESS")) {
        if (!customers.containsKey(username)) {
          customers.put(username, new Customer(username));
        }
        return "SUCCESS";
      }
      return result;
    } else {
      if (passwordManager.check(username, password)) {
        return "SUCCESS";
      }
      return "FAIL";
    }
  }

  public synchronized String processRequest(CustomerID customer, String command, String args) {
    if (customers.containsKey(customer.getName())) {
      switch (command) {
        case "SHOWMYACCOUNTS":
          return showMyAccounts(customer);
        default:
          return "FAIL";
      }
    }
    return "FAIL";
  }

  private String showMyAccounts(CustomerID customer) {
    return (customers.get(customer.getName())).accountsToString();
  }
}