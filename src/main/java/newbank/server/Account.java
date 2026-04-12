package newbank.server;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class Account {

  private final String accountName;
  private float balance;
  private String sortCode;
  private int accountNumber;

  public Account(String accountName, float openingBalance) {
    this.accountName = accountName;
    this.balance = openingBalance;
  }

  public String getName() {
    return accountName;
  }

  public float getBalance() {
    return this.balance;
  }

  /**
   * Updates the account balance.
   * Required for synchronization between database and memory.
   */
  public void setBalance(float balance) {
    this.balance = balance;
  }

  @Override
  public String toString() {
    return (accountName + ": " + balance);
  }

  public String deposit(float value, String name) {
    if (value < 0) return null;
    this.balance += value;
    // Transaction logging logic can be handled here or in NewBank
    return "SUCCESS";
  }

  public String withdrawOrPay(float value, String name) {
    if (value < 0 || balance < value) return null;
    this.balance -= value;
    return "SUCCESS";
  }
}