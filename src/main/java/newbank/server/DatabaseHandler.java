package newbank.server;

import java.sql.*;

public class DatabaseHandler {
    private static final String URL = "jdbc:sqlite:newbank.db";

    public DatabaseHandler() {
        createTables();
    }

    /**
     * Initializes the database schema by creating all necessary tables.
     * This includes users, transaction logs (ledger), and account balances.
     */
    private void createTables() {
        String userSql = "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL);";

        String transactionSql = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sender_username TEXT NOT NULL, " +
                "receiver_username TEXT, " +
                "amount REAL NOT NULL, " +
                "transaction_type TEXT NOT NULL, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);";

        String accountSql = "CREATE TABLE IF NOT EXISTS accounts (" +
                "username TEXT, " +
                "account_name TEXT, " +
                "balance REAL, " +
                "FOREIGN KEY(username) REFERENCES users(username));";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(userSql);
            stmt.execute(transactionSql);
            stmt.execute(accountSql);

            System.out.println("[DATABASE] All tables initialized successfully.");
        } catch (SQLException e) {
            System.err.println("[DATABASE] Schema initialization error: " + e.getMessage());
        }
    }

    /**
     * Required for NewBank.java to initialize a new account during registration.
     */
    public void saveNewAccount(String username, String accountName, double balance) {
        String sql = "INSERT INTO accounts(username, account_name, balance) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, accountName);
            pstmt.setDouble(3, balance);
            pstmt.executeUpdate();
            System.out.println("[DATABASE] Initial account created for: " + username);
        } catch (SQLException e) {
            System.err.println("[DATABASE] Failed to save new account: " + e.getMessage());
        }
    }

    public void addTransaction(String sender, String receiver, double amount, String type) {
        String sql = "INSERT INTO transactions(sender_username, receiver_username, amount, transaction_type) VALUES(?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sender);
            pstmt.setString(2, receiver);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, type);
            pstmt.executeUpdate();
            System.out.println("[LEDGER] Transaction recorded: " + type);
        } catch (SQLException e) {
            System.err.println("[LEDGER] Failed to record transaction: " + e.getMessage());
        }
    }

    public boolean addUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public ResultSet getAllData() {
        String sql = "SELECT users.username, accounts.account_name, accounts.balance " +
                "FROM users LEFT JOIN accounts ON users.username = accounts.username";
        try {
            Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println("[DATABASE] Failed to fetch system data: " + e.getMessage());
            return null;
        }
    }

    public void updateAccountBalance(String username, String accountName, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE username = ? AND account_name = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, username);
            pstmt.setString(3, accountName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DATABASE] Balance update error: " + e.getMessage());
        }
    }

    public String getTransactionHistory(String username) {
        String sql = "SELECT * FROM transactions WHERE sender_username = ? " +
                "OR receiver_username = ? ORDER BY timestamp DESC";
        StringBuilder history = new StringBuilder();

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                history.append(rs.getString("timestamp")).append(" | ")
                        .append(rs.getString("transaction_type")).append(" | ")
                        .append(rs.getDouble("amount")).append(" | Related: ")
                        .append(rs.getString("receiver_username") != null ? rs.getString("receiver_username") : "Self")
                        .append("\n");
            }
        } catch (SQLException e) {
            return "ERROR: Could not retrieve transaction history.";
        }

        return history.length() == 0 ? "No transaction history found." : history.toString();
    }

    public String getPassword(String username) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password");
            }
        } catch (SQLException e) {
            System.err.println("[DATABASE] Query error: " + e.getMessage());
        }
        return null;
    }
}