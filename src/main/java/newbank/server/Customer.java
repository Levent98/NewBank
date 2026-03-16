package newbank.server;

import java.util.ArrayList;

public class Customer {
  
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
  public boolean addAccount(String name) {
  for (Account a : accounts) {
    if (a.getName().equalsIgnoreCase(name)) {
      return false;
    }
    if (accounts.size() >=10) {
      return false;
    }
  }

  accounts.add(new Account(name, 0.0));
  return true;
}

}
