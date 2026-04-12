package newbank.server;

import java.util.HashMap;

public class NewBank {

  private static NewBank bank = null;
  private HashMap<String, Customer> customers;
  private DatabaseHandler db;
  private PasswordManager passwordManager;

  private NewBank() {
    customers = new HashMap<>();
    this.db = new DatabaseHandler(); // Ortak veritabanı bağlantısı
    this.passwordManager = new PasswordManager();
    addTestData();
  }

  public static NewBank getBank() {
    if (bank == null) {
      bank = new NewBank();
    }
    return bank;
  }

  private void addTestData() {
    // Test verilerini veritabanına eklemeyi deneyelim
    db.addUser("Bhagy", "bhagy");
  }

  public synchronized String handleLogin(String username, String password, boolean isNewUser) {
    if (isNewUser) {
      // Önce şifre kurallarına (PasswordManager) bakalım
      String result = passwordManager.set(username, password);
      if (result.startsWith("SUCCESS")) {
        // Şifre uygunsa veritabanına yazalım
        if (db.addUser(username, password)) {
          if (!customers.containsKey(username)) {
            customers.put(username, new Customer(username));
          }
          return "SUCCESS";
        } else {
          return "ERROR: Username already exists";
        }
      }
      return result;
    } else {
      // Login işlemi: Veritabanından şifreyi çek ve kontrol et
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
        default:
          return "FAIL";
      }
    }
    return "FAIL";
  }

  private String showMyAccounts(CustomerID customer) {
    return (customers.get(customer.getName())).accountsToString();
  }
}