package newbank;

import newbank.server.Account;
import newbank.server.CustomerID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void deposit() throws Exception {
        Account account = new Account("Holiday", 1000.0f);

        // value correctly addresses incorrect values
        assertEquals(null, account.deposit(-4.00F, "Test"));

        // value correctly creates a transaction and adds it to the AccountData
        assertEquals("SUCCESS", account.deposit(4.00F, "Test"));
        assertEquals(1004, account.getBalance());
    }

    @Test
    void withdrawOrPay() throws Exception {
        Account account = new Account("Holiday", 1000.0f);

        // value correctly addresses incorrect values
        assertEquals(null, account.withdrawOrPay(-4.00F,"Test"));
        assertEquals(null, account.withdrawOrPay(10000F,"Test"));

        // value correctly creates a transaction and adds it to the AccountData
        assertEquals("SUCCESS", account.withdrawOrPay(4.00F, "Test"));
        assertEquals(996, account.getBalance());
    }
}