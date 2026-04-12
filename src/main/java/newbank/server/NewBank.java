package newbank.server;

import java.sql.ResultSet;
import java.util.HashMap;

public class NewBank {

  private static NewBank bank = null;
  private HashMap<String, Customer> customers;
  private DatabaseHandler db;
  private PasswordManager passwordManager;

  private NewBank() {
    customers = new HashMap<>();
    this.db = new DatabaseHandler();
    this.passwordManager = new PasswordManager();
    loadDataFromDatabase();
  }

  public static NewBank getBank() {
    if (bank == null) {
      bank = new NewBank();
    }
    return bank;
  }

  private void loadDataFromDatabase() {
    try (ResultSet rs = db.getAllData()) {
      if (rs == null) return;
      while (rs.next()) {
        String username = rs.getString("username");
        String accountName = rs.getString("account_name");
        double balance = rs.getDouble("balance");

        if (!customers.containsKey(username)) {
          customers.put(username, new Customer(username));
        }
        if (accountName != null) {
          customers.get(username).addAccount(new Account(accountName, (float) balance));
        }
      }
      System.out.println("[SYSTEM] All customer data successfully synchronized from database.");
    } catch (Exception e) {
      System.err.println("[SYSTEM] Data synchronization error: " + e.getMessage());
    }
  }

  public synchronized String handleLogin(String username, String password, boolean isNewUser) {
    if (isNewUser) {
      String result = passwordManager.set(username, password);
      if (result.startsWith("SUCCESS")) {
        if (db.addUser(username, password)) {
          Customer newCustomer = new Customer(username);

          // --- FIX: Create Default "Main" Account ---
          // 1. Add to RAM
          newCustomer.addAccount(new Account("Main", 0.0f));
          customers.put(username, newCustomer);

          // 2. Add to SQLite (Ensure you have this method in DatabaseHandler)
          db.saveNewAccount(username, "Main", 0.0);

          // 3. Log to Ledger
          db.addTransaction(username, null, 0.0, "ACCOUNT_CREATED");

          return "SUCCESS";
        } else {
          return "ERROR: Username already exists";
        }
      }
      return result;
    } else {
      String storedPassword = db.getPassword(username);
      if (storedPassword != null && storedPassword.equals(password)) {
        if (!customers.containsKey(username)) {
          customers.put(username, new Customer(username));
        }
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

        case "SHOWMYHISTORY":
          return db.getTransactionHistory(customer.getName());

        case "PAY":
          String[] payArgs = args.split(" ");
          if (payArgs.length < 2) return "FAIL - Use: PAY <Receiver> <Amount>";

          String receiverName = payArgs[0];
          float amount;

          try {
            amount = Float.parseFloat(payArgs[1]);
          } catch (NumberFormatException e) {
            return "FAIL - Invalid amount format";
          }

          if (!customers.containsKey(receiverName)) return "FAIL - Receiver not found";

          Customer sender = customers.get(customer.getName());
          Customer receiver = customers.get(receiverName);

          Account senderAcc = sender.getAccount("Main");
          Account receiverAcc = receiver.getAccount("Main");

          if (senderAcc != null && receiverAcc != null) {
            if (senderAcc.getBalance() >= amount) {
              senderAcc.setBalance(senderAcc.getBalance() - amount);
              receiverAcc.setBalance(receiverAcc.getBalance() + amount);

              db.updateAccountBalance(customer.getName(), "Main", (double) senderAcc.getBalance());
              db.updateAccountBalance(receiverName, "Main", (double) receiverAcc.getBalance());

              db.addTransaction(customer.getName(), receiverName, (double) amount, "PAY");

              return "SUCCESS";
            }
            return "FAIL - Insufficient funds";
          }
          return "FAIL - Main account not found for one or both parties";
        default:
          return "FAIL - Unknown command";
      }
    }
    return "FAIL - Unauthorized";
  }

  private String showMyAccounts(CustomerID customer) {
    return (customers.get(customer.getName())).accountsToString();
  }
}