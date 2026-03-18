package newbank.server;

import java.util.HashMap;

import com.password4j.Argon2Function;
import com.password4j.Hash;
import com.password4j.Password;
import com.password4j.types.Argon2;

public class PasswordManager {

  // https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html
  // https://github.com/Password4j/password4j/wiki/Argon2
  private static final int MEMORY_KIB = 19 * 1024;
  private static final int ITERATIONS = 2;
  private static final int PARALLELISATION = 1;
  private static final int OUTPUT_LENGTH = 32;
  private static final int VERSION = 19;

  // for use with addRandomSalt
  private static final int SALT = 16; // bytes

  private static final Argon2Function ARGON2ID =
    Argon2Function.getInstance(MEMORY_KIB, ITERATIONS, PARALLELISATION, OUTPUT_LENGTH, Argon2.ID, VERSION);

  // Singleton pattern
  private static final PasswordManager passwordManager = new PasswordManager();
  private HashMap<String,String> passwords;

  private PasswordManager() {
    passwords = new HashMap<>();
    addTestData();
  }

  private void addTestData() {
    // The first String is the customer ID, the second the password
    Hash bhagyHash = Password.hash("bhagy").addRandomSalt(SALT).with(ARGON2ID);
    passwords.put("Bhagy", bhagyHash.getResult());
    
    Hash christinaHash = Password.hash("christina").addRandomSalt(SALT).with(ARGON2ID);
    passwords.put("Christina", christinaHash.getResult());
    
    // John has password "john" (raw), but we STORE a hash of it
    Hash johnHash = Password.hash("john").addRandomSalt(SALT).with(ARGON2ID);
    passwords.put("John", johnHash.getResult());
  }

  public static PasswordManager getPasswordManager() {
    return passwordManager;
  }

  // Could store the parameters to be more explicit in inputPassword & actualPassword
  public boolean check(String userName, String userProvidedPassword) {
    //return password.equals(passwords.get(userName));
    String hashFromDB = passwords.get(userName);
    if (hashFromDB == null) {
      return false;
    }
    return Password.check(userProvidedPassword, hashFromDB).with(ARGON2ID);
  }

}
