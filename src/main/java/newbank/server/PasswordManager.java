/* Minimal shape:

    Map<String, String> passwordHashByUser = new HashMap<>();
    boolean userExists(String userName)
    void register(String userName, String rawPassword) → hashes and stores
    boolean verify(String userName, String rawPassword) → checks hash
    void changePassword(String userName, String newRawPassword) → replace stored hash

    With password4j, the rough pattern is:

    Hash:
        String hash = Password.hash(rawPassword).addRandomSalt().withArgon2().getResult();
    Verify:
        boolean ok = Password.check(rawPassword, storedHash).withArgon2();
*/

package newbank.server;

import java.util.HashMap;

public class PasswordManager {

  // Singleton pattern
  private static final PasswordManager passwordManager = new PasswordManager();
  private HashMap<String,String> passwords;

  private PasswordManager() {
    passwords = new HashMap<>();
    addTestData();
  }

  private void addTestData() {
    // The first String is the customer ID, the second the password
    passwords.put("Bhagy", "bhagy");
    
    passwords.put("Christina", "christina");
    
    passwords.put("John", "john");
  }

  public static PasswordManager getPasswordManager() {
    return passwordManager;
  }

  // Could store the parameters to be more explicit in inputPassword & actualPassword
  public boolean check(String userName, String password) {
    return password.equals(passwords.get(userName));
  }

}
