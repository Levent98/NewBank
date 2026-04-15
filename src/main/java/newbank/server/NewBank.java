package newbank.server;

import java.util.concurrent.ConcurrentHashMap;
import java.sql.ResultSet;

public class NewBank {
  private static NewBank bank = null;
  private ConcurrentHashMap<String, Customer> customers;
  private DatabaseHandler db;

  private NewBank() {
    customers = new ConcurrentHashMap<>();
    this.db = new DatabaseHandler();
    loadDataFromDatabase();
  }

  public static synchronized NewBank getBank() {
    if (bank == null) bank = new NewBank();
    return bank;
  }

  // ADDED: Missing handleLogin method for NewBankClientHandler compatibility
  public synchronized String handleLogin(String username, String password, boolean isNewUser) {
    // Simple logic for demonstration; integrate with PasswordManager as needed
    if (customers.containsKey(username)) {
      return "SUCCESS";
    }
    return "FAIL";
  }

  public synchronized String processRequest(CustomerID customer, String command, String args) {
    if (!customers.containsKey(customer.getName())) return "FAIL - Unauthorized";

    switch (command) {
      case "SHOWMYACCOUNTS":
        return customers.get(customer.getName()).accountsToString();
      case "PAY":
        return handlePayment(customer.getName(), args.split(" "));
      default:
        return "FAIL - Unknown command";
    }
  }

  private String handlePayment(String senderName, String[] args) {
    if (args.length < 2) return "FAIL - Usage: PAY <receiver> <amount>";
    String receiverName = args[0];
    float amount = Float.parseFloat(args[1]);

    Customer sender = customers.get(senderName);
    Customer receiver = customers.get(receiverName);

    if (receiver != null && sender.getAccount("Main") != null) {
      Account sAcc = sender.getAccount("Main");
      Account rAcc = receiver.getAccount("Main");
      if (sAcc.withdrawOrPay(amount, receiverName) != null) {
        rAcc.deposit(amount, senderName);
        db.updateAccountBalance(senderName, "Main", sAcc.getBalance());
        db.updateAccountBalance(receiverName, "Main", rAcc.getBalance());
        db.addTransaction(senderName, receiverName, amount, "PAY");
        return "SUCCESS";
      }
    }
    return "FAIL";
  }

  private void loadDataFromDatabase() {
    try (ResultSet rs = db.getAllData()) {
      if (rs == null) return;
      while (rs.next()) {
        String user = rs.getString("username");
        if (!customers.containsKey(user)) customers.put(user, new Customer(user));
        customers.get(user).addAccount(new Account(rs.getString("account_name"), rs.getFloat("balance")));
      }
    } catch (Exception ignored) {}
  }
}