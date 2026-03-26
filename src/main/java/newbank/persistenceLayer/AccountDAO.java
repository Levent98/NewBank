package newbank.persistenceLayer;

import newbank.dataLayer.AccountData;
import newbank.server.CustomerID;
import newbank.server.Transaction;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public class AccountDAO {
    private final List<Transaction> accountTransactions;

    public AccountDAO(CustomerID customerID, String accountName) throws Exception {
        AccountData data = new AccountData();
        try{
            accountTransactions = data.getTransactions(customerID.getKey(), accountName);
        } catch (Exception e){
            throw new Exception("No Account exists for specified CustomerID and Account Name");
        }
        if(accountTransactions==null){
            throw new Exception("No Account exists for specified CustomerID and Account Name");
        }
    }

    public float getAccountBalance(){
        // Placeholder accountBalance until better test data is created
        return 1000;
    }

    public void addTransaction(Transaction transaction){
        accountTransactions.add(transaction);
        // When it comes to the database we will need to throw an exception if the addition was not possible.


    }
}