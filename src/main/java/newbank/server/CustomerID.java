package newbank.server;

public class CustomerID {

  private String key;
  private Role role;

  public enum Role { CUSTOMER, EMPLOYEE }

  public String getName() {
    return key;
  }

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