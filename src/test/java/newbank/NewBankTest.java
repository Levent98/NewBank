package newbank;

import org.junit.jupiter.api.Test;

import newbank.server.CustomerID;
import newbank.server.NewBank;

import static org.junit.jupiter.api.Assertions.*;

public class NewBankTest {

  @Test
    void addAccount_NewValidAccount_IsVisibleInList() {
      NewBank bank = NewBank.getBank();
      CustomerID customer = new CustomerID("Bhagy");

      bank.processRequest(customer, "NEWACCOUNT Holiday");

      String result = bank.processRequest(customer, "SHOWMYACCOUNTS");

      assertTrue(result.contains("Holiday"),
                "SHOWMYACCOUNTS should list the newly created account.");
    }

  @Test
    void processRequest_AccountCreated_ReturnsMessage() {
      NewBank bank = NewBank.getBank();
      CustomerID customer = new CustomerID("Bhagy");

      String result = bank.processRequest(customer, "NEWACCOUNT Travel");

      assertEquals("SUCCESS - a new account 'Travel' has been created.",result,
                "System should confirm successful account creation.");
    }

  @Test
    void processRequest_DuplicateAccount_ReturnsFail() {
      NewBank bank = NewBank.getBank();
      CustomerID customer = new CustomerID("Bhagy");

      // First creation succeeds
      bank.processRequest(customer, "NEWACCOUNT Bills");

      // Second creation with same name should fail
      String result = bank.processRequest(customer, "NEWACCOUNT Bills");

      assertEquals("FAIL - an error occured.", result,
                "System should notify user of an error when account creation fails.");
    }

  @Test
    void processRequest_MaximumTenAccounts_ReturnsFail() {
      NewBank bank = NewBank.getBank();
      CustomerID customer = new CustomerID("Christina");

      // Create 10 accounts
      for (int i = 1; i < 11; i++) {
        String result = bank.processRequest(customer, "NEWACCOUNT Acc" + i);
          assertTrue(result.startsWith("SUCCESS"),
                  "Account " + i + " should be created successfully.");
      }

      // Attempt the 11th
      String result = bank.processRequest(customer, "NEWACCOUNT TooMany");

      assertEquals("FAIL - an error occured.", result,
                "System should prevent creation of more than 10 accounts.");
    }
}


