package newbank.server;

import java.util.ArrayList;

public class Customer {
  private String fullName;
  private ArrayList<Account> accounts;

  public Customer(String fullName) {
    this.fullName = fullName;
    this.accounts = new ArrayList<>();
  }

  public Customer() {
    this.accounts = new ArrayList<>();
  }

  public String getName() {
    return fullName;
  }

  /**
   * Returns a formatted string listing all accounts and their balances.
   */
  public String accountsToString() {
    StringBuilder s = new StringBuilder();
    for (Account a : accounts) {
      s.append(a.toString()).append("\n");
    }
    return s.length() == 0 ? "No accounts found." : s.toString();
  }

  /**
   * Adds an existing Account object to the customer's profile.
   */
  public void addAccount(Account account) {
    accounts.add(account);
  }

  /**
   * Updates the balance of a specific account in memory.
   * @param accountName The name of the account to update.
   * @param amount The amount to add (positive) or subtract (negative).
   */
  public void updateBalance(String accountName, double amount) {
    for (Account a : accounts) {
      if (a.getName().equalsIgnoreCase(accountName)) {
        // Explicitly casting to float to match the Account class field type
        a.setBalance(a.getBalance() + (float) amount);
      }
    }
  }

  /**
   * Creates a new account with a 0.0 balance if the name is unique.
   * Limit: 10 accounts per customer.
   */
  public boolean addAccount(String name) {
    if (accounts.size() >= 10) return false;
    for (Account a : accounts) {
      if (a.getName().equalsIgnoreCase(name)) return false;
    }
    accounts.add(new Account(name, 0.0f));
    return true;
  }

  /**
   * Retrieves an Account object by its name.
   */
  public Account getAccount(String accountName) {
    for (Account account : accounts) {
      if (account.getName().equalsIgnoreCase(accountName)) {
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