package newbank.server;

import java.sql.*;

public class DatabaseHandler {
    private static final String URL = "jdbc:sqlite:newbank.db";

    public DatabaseHandler() {
        createTables();
    }

    private void createTables() {
        String userSql = "CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY, password TEXT NOT NULL);";
        String transactionSql = "CREATE TABLE IF NOT EXISTS transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, sender_username TEXT, receiver_username TEXT, amount REAL, transaction_type TEXT, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);";
        String accountSql = "CREATE TABLE IF NOT EXISTS accounts (username TEXT, account_name TEXT, balance REAL, FOREIGN KEY(username) REFERENCES users(username));";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(userSql);
            stmt.execute(transactionSql);
            stmt.execute(accountSql);
        } catch (SQLException e) {
            System.err.println("DB Initialization Error: " + e.getMessage());
        }
    }

    // UPDATED: Standardized method to update account balance
    public void updateAccountBalance(String username, String accountName, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE username = ? AND account_name = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, username);
            pstmt.setString(3, accountName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to sync balance: " + e.getMessage());
        }
    }

    // UPDATED: Standardized method to log transactions
    public void addTransaction(String sender, String receiver, double amount, String type) {
        String sql = "INSERT INTO transactions(sender_username, receiver_username, amount, transaction_type) VALUES(?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sender);
            pstmt.setString(2, receiver);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, type);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to log transaction: " + e.getMessage());
        }
    }

    public ResultSet getAllData() {
        try {
            Connection conn = DriverManager.getConnection(URL);
            String sql = "SELECT * FROM accounts";
            return conn.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            return null;
        }
    }
}