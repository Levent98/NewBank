package newbank.server;

import java.sql.Date;

public class Transaction {
    private String Name;
    private double value;
    private Date date;

    public Transaction(String name, double value, Date date) {
        Name = name;
        this.value = value;
        this.date = date;
    }

    public String getName() {
        return Name;
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
                "Name='" + Name + '\'' +
                ", value=" + value +
                ", date=" + date +
                '}';
    }
}
