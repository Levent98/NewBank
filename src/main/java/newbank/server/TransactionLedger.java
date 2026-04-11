package newbank.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransactionLedger {

    // Stores transactions per customer (key = customer name)
    private final HashMap<String, List<Transaction>> ledger;

    // Constructor
    public TransactionLedger() {
        ledger = new HashMap<>();
    }

    // Record a transaction for a given customer
    public void record(String customerName, Transaction transaction) {
        ledger
            .computeIfAbsent(customerName, k -> new ArrayList<>())
            .add(transaction);
    }

    // Retrieve all transactions for a customer
    public List<Transaction> getTransactions(String customerName) {
        return ledger.getOrDefault(customerName, new ArrayList<>());
    }

    // Admin only return all transactions for audit purposes.. private access modifier required?
    public HashMap<String, List<Transaction>> getAllTransactions() {
        return ledger;
    }
}