package newbank;

import newbank.server.Account;
import newbank.server.Customer;
import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.TransactionManager;
import org.junit.jupiter.api.Test;

import java.util.StringTokenizer;

import static org.junit.jupiter.api.Assertions.*;

class TransactionManagerTest {

    // Testing exception thrown when incorrect command given (should never occur)
    @Test
    void constructor_TestExceptionMessage_IncorrectCommand() {
        String request1 = "INCORRECT 200 Holiday \"Not an Account\"";
        Customer customer = new Customer();
        customer.addAccount(new Account("Main", 1000.0f));
        Exception exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request1);
        });
        assertEquals("ERROR: Incorrect command", exception.getMessage());
    }

    // Testing exception thrown when trying to move money from/to an account that doesn't exist
    @Test
    void constructor_TestExceptionMessage_AccountDoesNotExist() {
        String request1 = "MOVE 200 Holiday \"Not an Account\"";
        Customer customer = new Customer();
        customer.addAccount(new Account("Holiday", 1000.0f));
        Exception exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request1);
        });
        assertEquals("ERROR: The account \"Not an Account\" does not exist", exception.getMessage());

        String request2 = "MOVE 200 \"Not an Account\" Holiday";
        exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request2);
        });
        assertEquals("ERROR: The account \"Not an Account\" does not exist", exception.getMessage());

        String request3 = "MOVE 200 \"NotanAccount\" \"Not an Account\"";
        exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request3);
        });
        assertEquals("ERROR: The account \"NotanAccount\" does not exist", exception.getMessage());
    }

    // Testing exception thrown when request is not long enough
    // i.e. MOVE
    //      MOVE VALUE
    //      MOVE VALUE ACCOUNT1
    //      MOVE VALUE ACCOUNT1 ACCOUNT2 ACCOUNT3
    @Test
    void constructor_TestExceptionMessage_RequestHasTooFewArguments() {
        String request1 = "MOVE";
        Customer customer = new Customer();
        customer.addAccount(new Account("Holiday", 1000.0f));
        Exception exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request1);
        });
        assertEquals("ERROR: MOVE command must be in the format \"MOVE VALUE FROM TO\"", exception.getMessage());

        String request2 = "MOVE 200";
        exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request2);
        });
        assertEquals("ERROR: MOVE command must be in the format \"MOVE VALUE FROM TO\"", exception.getMessage());

        String request3 = "MOVE 200 ACCOUNT1";
        exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request3);
        });
        assertEquals("ERROR: MOVE command must be in the format \"MOVE VALUE FROM TO\"", exception.getMessage());

        String request4 = "MOVE 200 ACCOUNT1 ACCOUNT2 ACCOUNT3";
        exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request4);
        });
        assertEquals("ERROR: MOVE command must be in the format \"MOVE VALUE FROM TO\"", exception.getMessage());

        String request5 = "MOVE 200 \"ACCOUNT1 ACCOUNT2\"";
        exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request5);
        });
        assertEquals("ERROR: MOVE command must be in the format \"MOVE VALUE FROM TO\"", exception.getMessage());
    }

    // Testing exception thrown when request has incorrect monetary value format
    @Test
    void constructor_TestExceptionMessage_IncorrectNumericValue(){
        // Testing exception thrown by incorrect VALUE given
        String request1 = "MOVE 200.222 ACCOUNT1 ACCOUNT2";
        Customer customer = new Customer();
        customer.addAccount(new Account("Holiday", 1000.0f));
        Exception exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request1);
        });
        assertEquals("ERROR: Value is in the incorrect format. Please specify as an integer or a float with two decimal points.", exception.getMessage());


        String request2 = "MOVE .222 ACCOUNT1 ACCOUNT2";
        exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request2);
        });
        assertEquals("ERROR: Value is in the incorrect format. Please specify as an integer or a float with two decimal points.", exception.getMessage());
    }

    // Testing exception when request has missing quotes
    @Test
    void parseAccount_TestExceptionMessage_MissingQuotes(){
        String request1 = "MOVE 200 \"Holiday NotAnAccount";
        Customer customer = new Customer();
        Exception exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request1);
        });
        assertEquals("ERROR: An account starting with a '\"' must end with a '\"'", exception.getMessage());


        String request2 = "MOVE 200 Holiday \"Not an Account";
        exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request2);
        });
        assertEquals("ERROR: An account starting with a '\"' must end with a '\"'", exception.getMessage());
    }

    @Test
    void parseAccount_TestCorrectValues() throws Exception {
        Customer customer = new Customer();
        customer.addAccount(new Account("Holiday", 1000.0f));
        customer.addAccount(new Account("Current Account", 1000.0f));
        String request = "MOVE 200 Holiday \"Current Account\"";
        TransactionManager transactionManager = new TransactionManager(customer, request);
        String[] requests = {
                "MOVE ACCOUNT1 ACCOUNT2",
                "MOVE \"ACCOUNT2\" ACCOUNT2",
                "MOVE \"ACCOUNT2 A\" ACCOUNT2",
                "MOVE \"ACCOUNT2 A BB\" ACCOUNT2",
                "MOVE ACCOUNT3",
                "MOVE \"ACCOUNT4\"",
                "MOVE \"ACCOUNT4 A\"",
                "MOVE \"ACCOUNT4 A BB\""
        };
        String[] expectedResults = {
                "ACCOUNT1",
                "ACCOUNT2",
                "ACCOUNT2 A",
                "ACCOUNT2 A BB",
                "ACCOUNT3",
                "ACCOUNT4",
                "ACCOUNT4 A",
                "ACCOUNT4 A BB"
        };
        for(int i = 0; i<requests.length; i++){
            String req = requests[i];
            StringTokenizer st = new StringTokenizer(req, " ");
            st.nextToken();
            assertEquals(expectedResults[i],transactionManager.parseAccount(st));
        }
    }

    @Test
    void constructor_TestExceptionMessage_PayAccountDoesNotExist() {
        String request = "PAY FakeAccount John 100";
        Customer customer = new Customer();
        customer.addAccount(new Account("Checking", 250.0f));

        Exception exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request);
        });
        assertEquals("FAIL - Account name not valid", exception.getMessage());
    }

    @Test
    void constructor_TestExceptionMessage_PayRequestHasTooFewArguments() {
        Customer customer = new Customer();
        customer.addAccount(new Account("Checking", 250.0f));

        String request1 = "PAY";
        Exception exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request1);
        });
        assertEquals("ERROR: PAY command must be in the format \"PAY FROMACCOUNT PAYEE AMOUNT\"", exception.getMessage());

        String request2 = "PAY Checking";
        exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request2);
        });
        assertEquals("ERROR: PAY command must be in the format \"PAY FROMACCOUNT PAYEE AMOUNT\"", exception.getMessage());

        String request3 = "PAY Checking John";
        exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request3);
        });
        assertEquals("ERROR: PAY command must be in the format \"PAY FROMACCOUNT PAYEE AMOUNT\"", exception.getMessage());
    }

    @Test
    void constructor_TestExceptionMessage_PayIncorrectNumericValue() {
        Customer customer = new Customer();
        customer.addAccount(new Account("Checking", 250.0f));

        String request1 = "PAY Checking John 100.222";
        Exception exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request1);
        });
        assertEquals("ERROR: Value is in the incorrect format. Please specify as an integer or a float with two decimal points.", exception.getMessage());

        String request2 = "PAY Checking John .222";
        exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request2);
        });
        assertEquals("ERROR: Value is in the incorrect format. Please specify as an integer or a float with two decimal points.", exception.getMessage());
    }

    @Test
    void constructor_TestExceptionMessage_PayMaxPaymentExceeded() {
        Customer customer = new Customer();
        customer.addAccount(new Account("Checking", 5000.0f));

        String request = "PAY Checking John 4000";
        Exception exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request);
        });
        assertEquals("FAIL - Max Payment 3500.0", exception.getMessage());
    }

    @Test
    void payMoney_TestCorrectValues_InsufficientBalance() throws Exception {
        NewBank bank = NewBank.getBank();
        CustomerID customer = new CustomerID("John");

        String result = bank.processRequest(customer, "PAY", "Checking Christina 1000");
        assertEquals("FAIL - Insufficient balance", result);
    }

    @Test
    void payMoney_TestCorrectValues_InvalidRecipient() throws Exception {
        NewBank bank = NewBank.getBank();
        CustomerID customer = new CustomerID("Bhagy");

        String result = bank.processRequest(customer, "PAY", "Main FakeUser 100");
        assertEquals("FAIL - Account name not valid", result);
    }
// Add money 
    @Test
    void constructor_TestExceptionMessage_AddMoneyRequestHasTooFewArguments() {
        Customer customer = new Customer();
        customer.addAccount(new Account("Main", 1000.0f));

        String request1 = "ADDMONEY";
        Exception exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request1);
        });
        assertEquals("ERROR: ADDMONEY command must be in the format \"ADDMONEY TOACCOUNT AMOUNT SOURCE\"", exception.getMessage());

        String request2 = "ADDMONEY Main";
        exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request2);
        });
        assertEquals("ERROR: ADDMONEY command must be in the format \"ADDMONEY TOACCOUNT AMOUNT SOURCE\"", exception.getMessage());

        String request3 = "ADDMONEY Main 250.00";
        exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request3);
        });
        assertEquals("ERROR: ADDMONEY command must be in the format \"ADDMONEY TOACCOUNT AMOUNT SOURCE\"", exception.getMessage());
    }

    @Test
    void constructor_TestExceptionMessage_AddMoneyInvalidAccount() {
        Customer customer = new Customer();
        customer.addAccount(new Account("Main", 1000.0f));

        String request = "ADDMONEY FakeAccount 250.00 Payroll";
        Exception exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request);
        });
        assertEquals("FAIL - Account name not valid", exception.getMessage());
    }

    @Test
    void constructor_TestExceptionMessage_AddMoneyZeroAmount() {
        Customer customer = new Customer();
        customer.addAccount(new Account("Main", 1000.0f));

        String request = "ADDMONEY Main 0.00 Payroll";
        Exception exception = assertThrows(Exception.class, () -> {
            new TransactionManager(customer, request);
        });
        assertEquals("FAIL - Amount must be greater than zero", exception.getMessage());
    }

    @Test
    void addMoney_TestCorrectValues() throws Exception {
        Customer customer = new Customer();
        customer.addAccount(new Account("Main", 1000.0f));

        TransactionManager transactionManager =
                new TransactionManager(customer, "ADDMONEY Main 250.00 Payroll");

        String result = transactionManager.addMoney();

        assertEquals("SUCCESS - 250.00 added to Main", result);
        assertEquals(1250.0f, customer.getAccount("Main").getBalance());
    }

}
