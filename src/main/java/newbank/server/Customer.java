package newbank.server;

import java.util.ArrayList;

public class Customer {
  private CustomerID customerID;
  private String fullName;
  private ArrayList<Account> accounts;
  private ArrayList<Account> deactivatedAccounts;

  public Customer() {
    accounts = new ArrayList<>();
    deactivatedAccounts = new ArrayList<>();
  }

  public ArrayList<Account> getDeactivatedAccounts() {
    return deactivatedAccounts;
  }

  public ArrayList<Account> getAccounts() {
    return accounts;
  }

  public String accountsToString() {
    StringBuilder sb = new StringBuilder();

    for (Account a : accounts) {
      sb.append(a.toString()).append("|");
    }
    for (Account a : deactivatedAccounts) {
      sb.append(a.getName()).append("- Inactive|");
    }
    return sb.toString();
  }

  public void addAccount(Account account) {
    accounts.add(account);
  }

  public boolean addAccount(String name) {
    for (Account a : accounts) {
      if (a.getName().equals(name)) {
        return false;
      }
      if (accounts.size() > 10) {
        return false;
      }
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