package newbank.server;


import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class Account {

  private final String accountName;

  private float balance;
  private String sortCode;
  private int accountNumber ;

  /**
   * Constructor for creating a new account
   * @param accountName
   * @param openingBalance
   */
  public Account(String accountName, float openingBalance) {
    this.accountName = accountName;
    this.balance = openingBalance;
    // This bit should also create the account
  }

  public String getName() {
    return accountName;
  }

  @Override
  public String toString() {
    return (accountName + ": " + balance);
  }

  /**
   * Method used to add a transaction to the
   * @param value
   * @param name
   * @return returns null if the deposit was not successful, and
   * the string Successful if the deposit completed.
   */
  public String deposit(float value, String name){
    // checks to ensure value is positive
    if(value<=0){
      return null;
    }
    this.balance += value;
    Date date = Date.valueOf(LocalDate.now(ZoneOffset.UTC));
    Transaction transaction = new Transaction(name,value,date);

    // add transaction to database
    // TO DO

    return "SUCCESS";
  }

  /**
   * Method used to withdraw money from an account. Checks that the balance is
   * large enough.
   * @param value
   * @param name
   * @return
   */
  public String withdrawOrPay(float value, String name){
    // check to ensure value is positive
    if(value<0){
      return null;
    }
    // check to ensure balance is large enough
    if(balance<value){
      return null;
    }
    this.balance -= value;
    Date date = Date.valueOf(LocalDate.now(ZoneOffset.UTC));
    Transaction transaction = new Transaction(name,-value,date);

    // add transaction to database
    // TO DO

    return "SUCCESS";
  }

  public float getBalance(){
    return this.balance;
  }
}