package newbank.server;

import java.sql.Date;

public class Transaction {
    private String reference;
    private double value;
    private Date date;

    public Transaction(String name, double value, Date date) {
        reference = name;
        this.value = value;
        this.date = date;
    }

    public String getReference() {
        return reference;
    }

    public double getValue() {
        return value;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "Name='" + reference + '\'' +
                ", value=" + value +
                ", date=" + date +
                '}';
    }
}
