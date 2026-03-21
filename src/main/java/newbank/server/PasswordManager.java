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
  private static final int SALT = 16;

  private static final Argon2Function ARGON2ID =
    Argon2Function.getInstance(MEMORY_KIB, ITERATIONS, PARALLELISATION, OUTPUT_LENGTH, Argon2.ID, VERSION);

  // Singleton pattern
  private static final PasswordManager passwordManager = new PasswordManager();
  private HashMap<String,String> passwords;

  private PasswordManager() {
    passwords = new HashMap<>();
    // This prepopulates the in-memory hash table with some values and can be disabled later
    addTestData();
  }

  private void addTestData() {
    // The first String is the customer ID, the second the password
    Hash bhagyHash = Password.hash("bhagy").addRandomSalt(SALT).with(ARGON2ID);
    passwords.put("Bhagy", bhagyHash.getResult());
    
    Hash christinaHash = Password.hash("christina").addRandomSalt(SALT).with(ARGON2ID);
    passwords.put("Christina", christinaHash.getResult());
    
    Hash johnHash = Password.hash("john").addRandomSalt(SALT).with(ARGON2ID);
    passwords.put("John", johnHash.getResult());
  }

  public static PasswordManager getPasswordManager() {
    return passwordManager;
  }

  public boolean check(String userName, String userProvidedPassword) {
    if (!hasUserName(userName)) {
      return false;
    }
    String hashFromDB = passwords.get(userName);
    // a null value indicates a locker user
    if (hashFromDB == null) {
      return false;
    }
    return Password.check(userProvidedPassword, hashFromDB).with(ARGON2ID);
  }

  // checking if the user is authenticated before changing the password is the job of the caller
  public String set(String userName, String userProvidedPassword) {
    if (userName == null) {
      return "Error: the username is empty";
    } else if (userProvidedPassword == null) {
      passwords.put(userName, null);
      return "Created locked account";
    }

    Hash userHash = Password.hash(userProvidedPassword).addRandomSalt(SALT).with(ARGON2ID);
    // put() returns null if there was no previous such key
    if (passwords.put(userName, userHash.getResult()) == null) {
      return "Username and password created";
    } else {
      return "Password changed";
    }
  }

  public boolean hasUserName(String userName) {
    if (userName == null) {
      return false;
    }
    return passwords.containsKey(userName);
  }

  public void delUserName(String userName) {
    if (hasUserName(userName)) {
      passwords.remove(userName);
    }
  }

  public void lockUserName(String userName) {
    if (hasUserName(userName)) {
      passwords.put(userName, null);
    }
  }

}
