package newbank.server;

import java.util.ArrayList;

public class Customer {
  private String fullName;
  private ArrayList<Account> accounts;

  public Customer(String fullName) {
    this.fullName = fullName;
    this.accounts = new ArrayList<>();
  }

  public String getName() {
    return fullName;
  }

  public void addAccount(Account account) {
    accounts.add(account);
  }

  // UPDATED: Now uses the synchronized methods in Account.java
  public void updateBalance(String accountName, double amount) {
    for (Account a : accounts) {
      if (a.getName().equalsIgnoreCase(accountName)) {
        float currentBalance = a.getBalance();
        a.setBalance(currentBalance + (float) amount);
      }
    }
  }

  public String accountsToString() {
    StringBuilder s = new StringBuilder();
    for (Account a : accounts) {
      s.append(a.toString()).append("\n");
    }
    return s.length() == 0 ? "No accounts found." : s.toString();
  }

  public Account getAccount(String accountName) {
    for (Account a : accounts) {
      if (a.getName().equalsIgnoreCase(accountName)) {
        return a;
      }
    }
    return null;
  }

  // Helper for PAY operations
  public Account getFirstAccount() {
    return accounts.isEmpty() ? null : accounts.get(0);
  }
}