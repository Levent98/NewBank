package newbank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void catch_Account_Constructor_Exception() {
        // Check Exceptions for the Account constructor correctly throw Exceptions CustomerID and/or accountName
        // that do not exist
        Exception exception = assertThrows(Exception.class, () -> {
            new Account(new CustomerID("Not an ID"), "Holiday");
        });
        assertEquals("No Account exists for specified CustomerID and Account Name", exception.getMessage());

        exception = assertThrows(Exception.class, () -> {
            new Account(new CustomerID("Holiday"), "Something");
        });
        assertEquals("No Account exists for specified CustomerID and Account Name", exception.getMessage());

        exception = assertThrows(Exception.class, () -> {
            new Account(new CustomerID("Not an ID"), "Something");
        });
        assertEquals("No Account exists for specified CustomerID and Account Name", exception.getMessage());
    }

    @Test
    void deposit() throws Exception {
        Account account = new Account(new CustomerID("Bhagy"), "Holiday");

        // value correctly addresses incorrect values
        assertEquals(null, account.deposit(-4.00F, "Test"));

        // value correctly creates a transaction and adds it to the AccountData
        assertEquals("Successful", account.deposit(4.00F, "Test"));
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
        assertEquals("Successful", account.withdrawOrPay(4.00F, "Test"));
        assertEquals(996, account.getBalance());

        // To do: Add check to ensure that the transaction was added correctly

        // To do: Add check to ensure that the balance is updated correctly in the database
    }
}