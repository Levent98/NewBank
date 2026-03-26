package newbank.dataLayer;

import newbank.server.Transaction;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountData {
    private HashMap<String, Map<String, List<Transaction>>> accountData = new HashMap<>();

    public AccountData() {
        List<Transaction> transactions1 = new ArrayList<>();
        transactions1.add(new Transaction("Tesco Superstore Swindon", -54.99, d(2026, 3, 11)));
        transactions1.add(new Transaction("Costa Coffee", -2.80, d(2026, 3, 12)));
        transactions1.add(new Transaction("Acme Ltd Payroll", 2500.00, d(2026, 3, 15)));
        transactions1.add(new Transaction("Rent - Old Town Lettings", -920.00, d(2026, 3, 16)));
        transactions1.add(new Transaction("Spotify", -18.99, d(2026, 3, 17)));
        Map<String, List<Transaction>> accounts = new HashMap<>();
        accounts.put("Holiday",transactions1);

        List<Transaction> transactions2 = new ArrayList<>();
        transactions2.add(new Transaction("Tesco Superstore Swindon", -54.99, d(2026, 3, 11)));
        transactions2.add(new Transaction("Costa Coffee", -2.80, d(2026, 3, 12)));
        transactions2.add(new Transaction("Acme Ltd Payroll", 2500.00, d(2026, 3, 15)));
        transactions2.add(new Transaction("Rent - Old Town Lettings", -920.00, d(2026, 3, 16)));
        transactions2.add(new Transaction("Spotify", -18.99, d(2026, 3, 17)));
        accounts.put("Current Account",transactions2);
        accountData.put("Bhagy", accounts);

        List<Transaction> transactions3 = new ArrayList<>();
        transactions3.add(new Transaction("Tesco Superstore Swindon", -54.99, d(2026, 3, 11)));
        transactions3.add(new Transaction("Acme Ltd Payroll", 2500.00, d(2026, 3, 12)));
        transactions3.add(new Transaction("Costa Coffee", -2.80, d(2026, 3, 13)));
        transactions3.add(new Transaction("Rent - Old Town Lettings", -920.00, d(2026, 3, 16)));
        transactions3.add(new Transaction("Spotify", -18.99, d(2026, 3, 17)));
        Map<String, List<Transaction>> acc3 = new HashMap<>();
        acc3.put("Current Account", transactions3);
        accountData.put("Christina", acc3);


        List<Transaction> transactions4 = new ArrayList<>();
        transactions4.add(new Transaction("Amazon UK", -120.45, d(2026, 2, 28)));
        transactions4.add(new Transaction("Sainsbury's", -33.10, d(2026, 3, 2)));
        transactions4.add(new Transaction("Refund - Amazon UK", 150.00, d(2026, 3, 5)));
        transactions4.add(new Transaction("Trainline", -45.00, d(2026, 3, 8)));
        transactions4.add(new Transaction("Greggs", -12.50, d(2026, 3, 9)));
        Map<String, List<Transaction>> acc4 = new HashMap<>();
        acc4.put("Credit Card", transactions4);
        accountData.put("Steve", acc4);


        List<Transaction> transactions5 = new ArrayList<>();
        transactions5.add(new Transaction("Monzo Transfer", 450.00, d(2026, 3, 10)));
        transactions5.add(new Transaction("Savings Pot Transfer", -450.00, d(2026, 3, 10)));
        transactions5.add(new Transaction("Shell Fuel", -64.20, d(2026, 3, 13)));
        transactions5.add(new Transaction("Netflix", -29.99, d(2026, 3, 14)));
        Map<String, List<Transaction>> acc5 = new HashMap<>();
        acc5.put("Savings", transactions5);
        accountData.put("Daniel", acc3);
    }
    private static Date d(int year, int month, int day) {
        return Date.valueOf(LocalDate.of(year, month, day));
    }

    public List<Transaction> getTransactions(String customerID, String accountName) throws Exception {
        return accountData.get(customerID).get(accountName);
    }
}