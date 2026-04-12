package newbank;

import newbank.server.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import newbank.server.CustomerID;
import newbank.server.NewBank;

import static org.junit.jupiter.api.Assertions.*;

public class NewBankTest {

  @BeforeEach
  void reset() {
    NewBank.getBank().reset();
  }

  @Test
    void addAccount_NewValidAccount_IsVisibleInList() {
      NewBank bank = NewBank.getBank();
      CustomerID customer = new CustomerID("Bhagy");

      bank.processRequest(customer, "NEWACCOUNT", "Holiday");

      String result = bank.processRequest(customer, "SHOWMYACCOUNTS", "");

      assertTrue(result.contains("Holiday"),
                "SHOWMYACCOUNTS should list the newly created account.");
    }

  @Test
    void processRequest_AccountCreated_ReturnsMessage() {
      NewBank bank = NewBank.getBank();
      CustomerID customer = new CustomerID("Bhagy");

      String result = bank.processRequest(customer, "NEWACCOUNT", "Travel");

      assertEquals("SUCCESS - a new account 'Travel' has been created.",result,
                "System should confirm successful account creation.");
    }

  @Test
    void processRequest_DuplicateAccount_ReturnsFail() {
      NewBank bank = NewBank.getBank();
      CustomerID customer = new CustomerID("Bhagy");

      // First creation succeeds
      bank.processRequest(customer, "NEWACCOUNT", "Bills");

      // Second creation with same name should fail
      String result = bank.processRequest(customer, "NEWACCOUNT", "Bills");

      assertEquals("FAIL - an error occured.", result,
                "System should notify user of an error when account creation fails.");
    }

  @Test
    void processRequest_MaximumTenAccounts_ReturnsFail() {
      NewBank bank = NewBank.getBank();
      CustomerID customer = new CustomerID("Christina");

      // Create 10 accounts
      for (int i = 1; i < 11; i++) {
        String result = bank.processRequest(customer, "NEWACCOUNT", "Acc" + i);
          assertTrue(result.startsWith("SUCCESS"),
                  "Account " + i + " should be created successfully.");
      }

      // Attempt the 11th
      String result = bank.processRequest(customer, "NEWACCOUNT", "TooMany");

      assertEquals("FAIL - an error occured.", result,
                "System should prevent creation of more than 10 accounts.");
    }

    // Additional tests for PAY commands

      @Test
  void processRequest_PayValidRecipient_ReturnsSuccessMessage() {
    NewBank bank = NewBank.getBank();
    CustomerID customer = new CustomerID("Bhagy");

    String result = bank.processRequest(customer, "PAY", "Main John 100");

    assertEquals("SUCCESS - you sent John 100.00", result,
            "System should confirm successful payment.");
  }

  @Test
  void processRequest_PayInvalidSourceAccount_ReturnsFail() {
    NewBank bank = NewBank.getBank();
    CustomerID customer = new CustomerID("Bhagy");

    String result = bank.processRequest(customer, "PAY", "FakeAccount John 100");

    assertEquals("FAIL - Account name not valid", result,
            "System should notify user when the source account name is invalid.");
  }

  @Test
  void processRequest_PayInvalidRecipient_ReturnsFail() {
    NewBank bank = NewBank.getBank();
    CustomerID customer = new CustomerID("Bhagy");

    String result = bank.processRequest(customer, "PAY", "Main FakeUser 100");

    assertEquals("FAIL - Account name not valid", result,
            "System should notify user when the recipient user is invalid.");
  }

  @Test
  void processRequest_PayInsufficientBalance_ReturnsFail() {
    NewBank bank = NewBank.getBank();
    CustomerID customer = new CustomerID("John");

    String result = bank.processRequest(customer, "PAY", "Checking Christina 1000");

    assertEquals("FAIL - Insufficient balance", result,
            "System should notify user when balance is insufficient.");
  }

  @Test
  void processRequest_PayMalformedCommand_ReturnsFail() {
    NewBank bank = NewBank.getBank();
    CustomerID customer = new CustomerID("John");

    String result = bank.processRequest(customer, "PAY", "Checking John");

    assertEquals("ERROR: PAY command must be in the format \"PAY FROMACCOUNT PAYEE AMOUNT\"", result,
        "System should notify user when PAY command format is invalid.");
  }

  @Test
  void processRequest_PayAboveMaxLimit_ReturnsFail() {
    NewBank bank = NewBank.getBank();
    CustomerID customer = new CustomerID("Bhagy");

    String result = bank.processRequest(customer, "PAY", "Main John 4000");

    assertEquals("FAIL - Max Payment 3500.0", result,
            "System should prevent payments above the maximum allowed amount.");
  }

    // US08 VIEWALL tests
    @Test
  void processRequest_ViewAll_AsAdmin_ReturnsCustomerList() {
    NewBank bank = NewBank.getBank();
    CustomerID admin = bank.checkLogInDetails("Admin", "admin");

    String result = bank.processRequest(admin, "VIEWALL", "");

    assertTrue(result.contains("John"),
            "VIEWALL should include customer John.");
    assertTrue(result.contains("Christina"),
            "VIEWALL should include customer Christina.");
    assertTrue(result.contains("Bhagy"),
            "VIEWALL should include customer Bhagy.");
  }
  // customer cannot access VIEWALL
  @Test
  void processRequest_ViewAll_AsCustomer_ReturnsError() {
    NewBank bank = NewBank.getBank();

    CustomerID customer = bank.checkLogInDetails("John", "john");

    String result = bank.processRequest(customer, "VIEWALL", "");

    assertEquals("Command not recognised", result,
            "Customers should not be allowed to use VIEWALL.");
  }

  // null user check
  @Test
  void processRequest_ViewAll_NoUser_ReturnsError() {
    NewBank bank = NewBank.getBank();

    String result = bank.processRequest(null, "VIEWALL", "");

    assertEquals("Command not recognised", result,
            "Unauthenticated users should not be allowed to use VIEWALL.");
  }

// The scenarios to be tested for the deactivateAccount:
// Negative balance
// 1 account with balance
// 1 account without balance
// More than 1 account with balance
// More than 1 account without balance
// Account does not exist
@Test
void processRequest_DeactivateAccount(){
  NewBank bank = NewBank.getBank();
  CustomerID customerID = new CustomerID("Bhagy");

    // 1 - Negative balance
    assertEquals("FAILURE - Credit Card has negative balance",bank.processRequest(customerID,"DEACTIVATE","Credit Card"));

    // 2 - One account with balance
    bank.processRequest(customerID, "MOVE", "100 Main \"Credit Card\"");
    bank.processRequest(customerID,"DEACTIVATE","Credit Card");
    assertEquals("FAILURE - Main is the only active account, with a balance of 900.0. Balance must be 0.",bank.processRequest(customerID,"DEACTIVATE","Main"));

    // 3 - One account without balance
    bank.processRequest(customerID, "NEWACCOUNT", "Account 1");
    assertEquals("SUCCESS - Account 1 deactivated.",bank.processRequest(customerID,"DEACTIVATE","Account 1"));

    // 4 - More than 1 account + with balance
    // deactivate Main and transfer to Account 2
    bank.processRequest(customerID, "NEWACCOUNT", "Account 2");
    assertEquals("SUCCESS - Main deactivated. Remaining balance of 900.0 moved to Account 2",bank.processRequest(customerID,"DEACTIVATE","Main"));
    assertEquals(true,bank.processRequest(customerID, "SHOWMYACCOUNTS","").contains("Account 2: 900.0"));
    // deactivate Account 3 and transfer to Account 2
    bank.processRequest(customerID, "NEWACCOUNT", "Account 3");
    bank.processRequest(customerID, "MOVE", "900 \"Account 2\" \"Account 3\"");
    assertEquals("SUCCESS - Account 3 deactivated. Remaining balance of 900.0 moved to Account 2",bank.processRequest(customerID,"DEACTIVATE","Account 3"));
    assertEquals(true,bank.processRequest(customerID, "SHOWMYACCOUNTS","").contains("Account 2: 900.0"));

    // 5 - More than 1 account + without balance
    // create new account
    bank.processRequest(customerID, "NEWACCOUNT", "Account 4");
    assertEquals("SUCCESS - Account 4 deactivated.",bank.processRequest(customerID,"DEACTIVATE","Account 4"));
    assertEquals(true,bank.processRequest(customerID, "SHOWMYACCOUNTS","").contains("Account 2: 900.0"));

    // 6 - Account does not exist
    assertEquals("FAILURE - Account 4 does not exist",bank.processRequest(customerID,"DEACTIVATE","Account 4"));

  }
}

