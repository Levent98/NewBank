package newbank.server;

public class Account {
  private final String accountName;
  private float balance;

  public Account(String accountName, float openingBalance) {
    this.accountName = accountName;
    this.balance = openingBalance;
  }

  public String getName() {
    return accountName;
  }

  public synchronized float getBalance() {
    return this.balance;
  }

  // ADDED: This method was missing, causing the Customer.java build error
  public synchronized void setBalance(float balance) {
    this.balance = balance;
  }

  public synchronized String deposit(float value, String name) {
    if (value < 0) return null;
    this.balance += value;
    return "SUCCESS";
  }

  public synchronized String withdrawOrPay(float value, String name) {
    if (value < 0 || balance < value) return null;
    this.balance -= value;
    return "SUCCESS";
  }

  @Override
  public String toString() {
    return (accountName + ": " + balance);
  }
}