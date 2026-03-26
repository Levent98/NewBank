package newbank.server;

import java.util.ArrayList;

public class Customer {
  private CustomerID customerID;
  private String fullName;
  private ArrayList<Account> accounts;

  public Customer() {
    accounts = new ArrayList<>();
  }

  public String accountsToString() {
    String s = "";
    for(Account a : accounts) {
      s += a.toString();
    }
    return s;
  }

  public void addAccount(Account account) {
    accounts.add(account);
  }
  // Adds the new account as long as there are no more than 10 accounts already setup
  public boolean addAccount(String name) {
    for (Account a : accounts) {
      if (a.getName().equalsIgnoreCase(name)) {
        return false;
      }
      if (accounts.size()>10) {
        return false;
      }
    }
    // Default opening balance set to 0.0
    accounts.add(new Account(name, 0.0));
    return true;
  }
}