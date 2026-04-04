package newbank.server;

import java.util.HashMap;
import java.util.StringTokenizer;

public class TransactionManager {
    private final Customer customer;
    private final HashMap<String, Customer> customers;
    private Customer recipientCustomer;
    private Account recipientAccount;
    private Account fromAccount;
    private Account toAccount;
    private float value;
    private String payee;
    private final String request;
    private static final float MAX_PAYMENT = 3500.0f;

    public TransactionManager(Customer customer, String request) throws Exception {
    this(customer, null, request);
    }

    public TransactionManager(Customer customer, HashMap<String, Customer> customers, String request) throws Exception {
        this.customer = customer;
        this.customers = customers;
        this.request = request;

        if(request.startsWith("MOVE")){
            checkMoveCommand();
        }
        else if(request.startsWith("PAY")){
            checkPayCommand();
        }
        else{
            throw new Exception("ERROR: Incorrect command");
        }
    }

    public void checkMoveCommand() throws Exception {
        String from;
        String to;
        StringTokenizer st = new StringTokenizer(request, " ");

        // Skip the string MOVE
        st.nextToken();

        String formatErrorMessage = "ERROR: MOVE command must be in the format \"MOVE VALUE FROM TO\"";
        // Check that the numeric VALUE is in the correct format
        if (!st.hasMoreTokens()) {
            throw new Exception(formatErrorMessage);
        }
        String token = st.nextToken();
        if (!token.matches("\\d+(\\.\\d{2})?")) {
            throw new Exception("ERROR: Value is in the incorrect format. Please specify as an integer or a float with two decimal points.");
        }
        this.value = Float.parseFloat(token);

        // Check for next tokens for an account with correct format
        if (!st.hasMoreTokens()) {
            throw new Exception(formatErrorMessage);
        }
        // Check for a FROM account
        from = parseAccount(st);

        // Check for next tokens for an account with correct format
        if (!st.hasMoreTokens()) {
            throw new Exception(formatErrorMessage);
        }
        // Check for a TO account
        to = parseAccount(st);

        // Check that there are no more tokens
        if (st.hasMoreTokens()) {
            throw new Exception(formatErrorMessage);
        }

        // Create account objects
        this.fromAccount = customer.getAccount(from);
        if(fromAccount==null){
            throw new Exception("ERROR: The account \"" + from + "\" does not exist");
        }
        this.toAccount = customer.getAccount(to);
        if(toAccount==null){
            throw new Exception("ERROR: The account \"" + to + "\" does not exist");
        }
    }

    public String parseAccount(StringTokenizer st) throws Exception {
        // Create StringBuilder
        StringBuilder builder = new StringBuilder();

        String token = st.nextToken();
        if(token.charAt(0)=='"'){
            if(token.charAt(token.length()-1) == '"'){
                builder.append(token,1,token.length()-1);
            }
            else{
                builder.append(token,1,token.length());
                do {
                    // Another check that ensures an open quote has an end quote
                    if(!st.hasMoreTokens()){
                        throw new Exception("ERROR: An account starting with a '\"' must end with a '\"'");
                    }
                    token = st.nextToken();
                    builder.append(" ").append(token);
                } while (token.charAt(token.length()-1)!='"');
                // Delete the quotes from the end of the account name
                builder.deleteCharAt(builder.length()-1);
            }
        }
        else builder.append(token);

        return builder.toString();
    }

    /**
     * Move money between the accounts of a customer
     * @return
     */
    public String moveMoney(){
        // Withdraw and Deposit
        // The FROM account must have enough balance, otherwise returns null
        if(fromAccount.withdrawOrPay(value, "internal transfer")!=null){
            toAccount.deposit(value, "internal transfer");
        }
        else{
            return "FAILURE: " + fromAccount.getName() +  " has a balance of £" + String.format("%.2f",fromAccount.getBalance()) + ", which is insufficient";
        }
        return "SUCCESS: £" + String.format("%.2f",value) + " transferred from " + fromAccount.getName() + " to " + toAccount.getName();
    }

    private void checkPayCommand() throws Exception {
    String from;
    StringTokenizer st = new StringTokenizer(request, " ");

    st.nextToken();

    String formatErrorMessage =
        "ERROR: PAY command must be in the format \"PAY FROMACCOUNT PAYEE AMOUNT\"";

    if (!st.hasMoreTokens()) {
        throw new Exception(formatErrorMessage);
    }
    from = parseAccount(st);

    if (!st.hasMoreTokens()) {
        throw new Exception(formatErrorMessage);
    }
    this.payee = parseAccount(st);

    if (!st.hasMoreTokens()) {
        throw new Exception(formatErrorMessage);
    }
    String token = st.nextToken();

    if (!token.matches("\\d+(\\.\\d{2})?")) {
        throw new Exception("ERROR: Value is in the incorrect format. Please specify as an integer or a float with two decimal points.");
    }
    this.value = Float.parseFloat(token);

    if (value > MAX_PAYMENT) {
        throw new Exception("FAIL - Max Payment " + MAX_PAYMENT);
    }

    if (st.hasMoreTokens()) {
        throw new Exception(formatErrorMessage);
    }

    this.fromAccount = customer.getAccount(from);
    if (fromAccount == null) {
        throw new Exception("FAIL - Account name not valid");
    }

    if (customers == null) {
        throw new Exception("FAIL - System error");
    }

    this.recipientCustomer = customers.get(payee);
    if (recipientCustomer == null) {
        throw new Exception("FAIL - Account name not valid");
    }

    this.recipientAccount = recipientCustomer.getFirstAccount();
    if (recipientAccount == null) {
        throw new Exception("FAIL - System error");
    }
}


     public String payMoney() {
    if (fromAccount.withdrawOrPay(value, payee) != null) {
        recipientAccount.deposit(value, "payment received");
        return "SUCCESS - you sent " + payee + " " + String.format("%.2f", value);
    }
    else{
        return "FAIL - Insufficient balance";
    }

  }
    
}