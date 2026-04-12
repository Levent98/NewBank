package newbank.server;

import java.util.ArrayList;

public class Customer {
  private CustomerID customerID;
  private String fullName;
  private ArrayList<Account> accounts;


  public Customer(String fullName) {
    this.fullName = fullName;
    this.customerID = new CustomerID(fullName);
    this.accounts = new ArrayList<>();
  }


  public Customer() {
    this.accounts = new ArrayList<>();
  }


  public String getName() {
    return fullName;
  }

  public String accountsToString() {
    StringBuilder s = new StringBuilder();
    for (Account a : accounts) {
      s.append(a.toString()).append("\n");
    }
    return s.length() == 0 ? "No accounts found." : s.toString();
  }

  public void addAccount(Account account) {
    accounts.add(account);
  }

  public boolean addAccount(String name) {
    if (accounts.size() >= 10) return false;
    for (Account a : accounts) {
      if (a.getName().equalsIgnoreCase(name)) return false;
    }
    accounts.add(new Account(name, 0.0f));
    return true;
  }

 
  public Account getAccount(String accountName) {
    for (Account account : accounts) {
      if (account.getName().equals(accountName)) {
        return account;
      }
    }
    return null;
  }

  public Account getFirstAccount() {
    if (accounts.isEmpty()) {
      return null;
    }
    return accounts.get(0);
  }
}