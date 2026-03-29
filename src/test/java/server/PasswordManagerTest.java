package newbank;

import newbank.server.PasswordManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

//import newbank.server.PasswordManager;

public class PasswordManagerTest {

  private PasswordManager passwords;

  @BeforeEach
  void setup() {
  }

  @Test
  void testCheckPassword() {
    passwords = PasswordManager.getPasswordManager();
    String username = "alice";
    String password = "secret123456789";

    passwords.set(username, password);

    // Test that the same password checks out
    assertTrue(passwords.check(username, password), "Password should verify for correct credential.");

    // Test that a wrong password is rejected
    assertFalse(passwords.check(username, "wrongpassword"), "Password should not verify for wrong credential.");
  }

  @Test
  void testChangePassword() {
    passwords = PasswordManager.getPasswordManager();
    String username = "alice";
    String password1 = "secret123456789";
    String password2 = "secret456789123";

    passwords.set(username, password1);

    // Test that the same password checks out
    assertTrue(passwords.check(username, password1), "Password should verify for correct credential.");

    // Change password and retest
    passwords.set(username, password2);
    assertTrue(passwords.check(username, password2), "Password should verify for correct credential.");
  }

  @Test
  void testLockUser() {
    passwords = PasswordManager.getPasswordManager();
    String username = "alice";
    String password = "secret123456789";

    // All tests should be self contained, need to re populate the table
    passwords.set(username, password);

    // Test that the same password checks out
    assertTrue(passwords.check(username, password), "Password should still verify for correct credential (database persistent in memory)");

    passwords.lockUserName(username);

    // Should now fail
    assertFalse(passwords.check(username, password), "This should now fail");
  }

  @Test
  void testDelUser() {
    passwords = PasswordManager.getPasswordManager();
    String username = "alice";
    String password = "secret123456789";

    // All tests should be self contained, need to re populate the table
    passwords.set(username, password);

    // Test that the same password checks out
    assertTrue(passwords.check(username, password), "Password should still verify for correct credential (database persistent in memory)");

    passwords.delUserName(username);

    // Should now fail
    assertFalse(passwords.check(username, password), "This should now fail");
  }

  @Test
  void hasUserName() {
    passwords = PasswordManager.getPasswordManager();
    String username1 = "alice";
    String password1 = "secret123456789";
    String username2 = "john";

    // All tests should be self contained, need to re populate the table
    passwords.set(username1, password1);
    // Test that the same password checks out
    assertTrue(passwords.hasUserName(username1), "Checking if true for a full record");

    passwords.set(username2, null);
    assertTrue(passwords.hasUserName(username2), "Checking if true for a record with a null value (locked)");
  }

  // Add more specific tests for hashing details if you want!

}
