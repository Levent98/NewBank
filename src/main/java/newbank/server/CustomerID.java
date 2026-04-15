package newbank.server;

public class CustomerID {

  public enum Role { CUSTOMER, EMPLOYEE }

  private String key;
  private Role role;
  
  // defaulting to CUSTOMER so existing code should continue to run
  public CustomerID(String key) {
    this(key, Role.CUSTOMER);
  }

  public CustomerID(String key, Role role) {
    this.key  = key;
    this.role = role;
  }

  public Role getRole() {
    return role;
  }

  public boolean isEmployee() {
    return role == Role.EMPLOYEE;
  }
  
  public String getKey() {
    return key;
  }
}
