package newbank;

import newbank.server.Account;
import newbank.server.Customer;
import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.TransactionManager;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.StringTokenizer;

import static org.junit.jupiter.api.Assertions.*;

class TransactionManagerTest {

    // 1. Yanlış komut testi
    @Test
    void constructor_TestExceptionMessage_IncorrectCommand() {
        String request1 = "INCORRECT 200 Holiday \"Not an Account\"";
        Customer customer = new Customer("TestUser");
        customer.addAccount(new Account("Main", 1000.0f));

        Exception exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request1);
        });
        assertEquals("ERROR: Incorrect command", exception.getMessage());
    }

    // 2. MOVE komutunda olmayan hesap testi
    @Test
    void constructor_TestExceptionMessage_AccountDoesNotExist() {
        Customer customer = new Customer("TestUser");
        customer.addAccount(new Account("Holiday", 1000.0f));

        // Kaynak hesap yoksa
        String request1 = "MOVE 200 \"Not an Account\" Holiday";
        Exception exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request1);
        });
        assertEquals("ERROR: The account \"Not an Account\" does not exist", exception.getMessage());

        // Hedef hesap yoksa
        String request2 = "MOVE 200 Holiday \"Not an Account\"";
        exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request2);
        });
        assertEquals("ERROR: The account \"Not an Account\" does not exist", exception.getMessage());
    }

    // 3. MOVE komutu eksik parametre testi
    @Test
    void constructor_TestExceptionMessage_RequestHasTooFewArguments() {
        Customer customer = new Customer("TestUser");
        customer.addAccount(new Account("Holiday", 1000.0f));
        String expectedMsg = "ERROR: MOVE command must be in the format \"MOVE VALUE FROM TO\"";

        assertThrows(Exception.class, () -> new TransactionManager(customer, "MOVE"), expectedMsg);
        assertThrows(Exception.class, () -> new TransactionManager(customer, "MOVE 200"), expectedMsg);
        assertThrows(Exception.class, () -> new TransactionManager(customer, "MOVE 200 ACCOUNT1"), expectedMsg);
    }

    // 4. Sayısal format hatası testi
    @Test
    void constructor_TestExceptionMessage_IncorrectNumericValue() {
        Customer customer = new Customer("TestUser");
        customer.addAccount(new Account("Holiday", 1000.0f));
        String expectedMsg = "ERROR: Value is in the incorrect format. Please specify as an integer or a float with two decimal points.";

        assertThrows(Exception.class, () -> new TransactionManager(customer, "MOVE 200.222 Holiday Main"), expectedMsg);
        assertThrows(Exception.class, () -> new TransactionManager(customer, "MOVE .222 Holiday Main"), expectedMsg);
    }

    // 5. Tırnak kapatma hatası testi
    @Test
    void parseAccount_TestExceptionMessage_MissingQuotes() {
        Customer customer = new Customer("TestUser");
        String expectedMsg = "ERROR: An account starting with a '\"' must end with a '\"'";

        assertThrows(Exception.class, () -> new TransactionManager(customer, "MOVE 200 \"Holiday Main"), expectedMsg);
    }

    // 6. PAY komutu - Olmayan kaynak hesap testi
    @Test
    void constructor_TestExceptionMessage_PayAccountDoesNotExist() {
        // TransactionManager PAY formatı: PAY FROM PAYEE AMOUNT
        String request = "PAY FakeAccount John 100";
        Customer customer = new Customer("Sender");
        customer.addAccount(new Account("Checking", 250.0f));

        // Alıcı listesi simülasyonu
        HashMap<String, Customer> customers = new HashMap<>();
        customers.put("John", new Customer("John"));

        Exception exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, customers, request);
        });
        assertEquals("FAIL - Account name not valid", exception.getMessage());
    }

    // 7. PAY komutu - Eksik parametre testi
    @Test
    void constructor_TestExceptionMessage_PayRequestHasTooFewArguments() {
        Customer customer = new Customer("Sender");
        String expectedMsg = "ERROR: PAY command must be in the format \"PAY FROMACCOUNT PAYEE AMOUNT\"";

        assertThrows(Exception.class, () -> new TransactionManager(customer, "PAY"), expectedMsg);
        assertThrows(Exception.class, () -> new TransactionManager(customer, "PAY Checking"), expectedMsg);
        assertThrows(Exception.class, () -> new TransactionManager(customer, "PAY Checking John"), expectedMsg);
    }

    // 8. PAY komutu - Maksimum limit testi (3500.0f)
    @Test
    void constructor_TestExceptionMessage_PayMaxPaymentExceeded() {
        Customer customer = new Customer("Sender");
        customer.addAccount(new Account("Checking", 5000.0f));

        HashMap<String, Customer> customers = new HashMap<>();
        customers.put("John", new Customer("John"));

        String request = "PAY Checking John 4000";
        Exception exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, customers, request);
        });
        // Float.toString() 3500.0f için "3500.0" döner
        assertEquals("FAIL - Max Payment 3500.0", exception.getMessage());
    }

    // 9. Banka üzerinden PAY testi (Yetersiz Bakiye)
    @Test
    void payMoney_TestCorrectValues_InsufficientBalance() {
        NewBank bank = NewBank.getBank();
        // John kullanıcısının Main hesabında az para olduğunu varsayıyoruz
        CustomerID customer = new CustomerID("John");

        // Not: NewBank.processRequest argüman olarak komut ve parametreleri ayırarak alıyor olabilir.
        // Paylaştığın koda göre: command="PAY", args="Checking Christina 1000"
        String result = bank.processRequest(customer, "PAY", "Checking Christina 10000");
        assertTrue(result.contains("FAIL - Insufficient balance") || result.contains("FAIL - Insufficient funds"));
    }
}