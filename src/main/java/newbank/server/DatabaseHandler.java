package newbank.server;

import java.sql.*;

public class DatabaseHandler {
    private static final String URL = "jdbc:sqlite:newbank.db";

    public DatabaseHandler() {
        createTables();
    }

    private void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT NOT NULL);";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Veritabanı oluşturma hatası: " + e.getMessage());
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
            // Kullanıcı zaten varsa veya başka bir hata oluşursa false döner
            return false;
        }
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
            System.out.println("Sorgu hatası: " + e.getMessage());
        }
        return null;
    }
}