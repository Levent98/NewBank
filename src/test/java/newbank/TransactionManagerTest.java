package newbank;

import newbank.server.Account;
import newbank.server.Customer;
import newbank.server.CustomerID;
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

}