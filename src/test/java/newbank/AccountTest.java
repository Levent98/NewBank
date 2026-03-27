package newbank;

import newbank.server.Account;
import newbank.server.CustomerID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void catch_Account_Constructor_Exception() {
        // Check Exceptions for the Account constructor correctly throw Exceptions CustomerID and/or accountName
        // that do not exist
        Exception exception = assertThrows(Exception.class, () -> {
            new Account(new CustomerID("Bhagy"), "Something");
        });
        assertEquals("ERROR: The account \"Something\" does not exist", exception.getMessage());
    }

    @Test
    void deposit() throws Exception {
        Account account = new Account(new CustomerID("Bhagy"), "Holiday");

        // value correctly addresses incorrect values
        assertEquals(null, account.deposit(-4.00F, "Test"));

        // value correctly creates a transaction and adds it to the AccountData
        assertEquals("SUCCESS", account.deposit(4.00F, "Test"));
        assertEquals(1004, account.getBalance());

        // To do: Add check to ensure that the transaction was added correctly

        // To do: Add check to ensure that the balance is updated correctly in the database
    }

    @Test
    void withdrawOrPay() throws Exception {
        Account account = new Account(new CustomerID("Bhagy"), "Holiday");

        // value correctly addresses incorrect values
        assertEquals(null, account.withdrawOrPay(-4.00F,"Test"));
        assertEquals(null, account.withdrawOrPay(10000F,"Test"));

        // value correctly creates a transaction and adds it to the AccountData
        assertEquals("SUCCESS", account.withdrawOrPay(4.00F, "Test"));
        assertEquals(996, account.getBalance());

        // To do: Add check to ensure that the transaction was added correctly

        // To do: Add check to ensure that the balance is updated correctly in the database
    }
}