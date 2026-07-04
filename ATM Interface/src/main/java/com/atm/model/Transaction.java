package com.atm.model;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a single financial transaction (e.g., deposit, withdrawal).
 * Contains details such as transaction ID, amount, timestamp, and type.
 */
public class Transaction implements Comparable<Transaction> {

    private final String transactionId;
    private final TransactionType type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime timestamp;
    private final String description;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Public constructor for creating a new transaction.
     * transactionId is auto-generated (first 8 chars of UUID) and timestamp is set to now.
     *
     * @param type         the kind of transaction
     * @param amount       the amount involved (0.0 for balance inquiries)
     * @param balanceAfter the account balance snapshot after this transaction
     * @param description  human-readable note
     */
    public Transaction(TransactionType type, double amount, double balanceAfter, String description) {
        this.transactionId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
        this.description = description;
    }

    /**
     * Private constructor used internally for reconstructing a Transaction from CSV.
     */
    private Transaction(String transactionId, TransactionType type, double amount, 
                        double balanceAfter, LocalDateTime timestamp, String description) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = timestamp;
        this.description = description;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int compareTo(Transaction other) {
        // Newest first (descending order)
        return other.timestamp.compareTo(this.timestamp);
    }

    private String formatAmount(double value) {
        // Formats amount with comma separators
        return new DecimalFormat("#,##0.00").format(value);
    }

    @Override
    public String toString() {
        String dateStr = timestamp.format(DATE_FORMATTER);
        String amtStr = amount == 0.0 ? "₹0.00" : type.getSymbol() + "₹" + formatAmount(Math.abs(amount));
        return String.format("[%s] TXN#%s | %s | %s | Bal: ₹%s", 
                dateStr, transactionId, type.name(), amtStr, formatAmount(balanceAfter));
    }

    /**
     * Serializes the transaction to a CSV string.
     * 
     * @return comma-separated string representation
     */
    public String toCSV() {
        String safeDescription = description != null ? description.replace("\"", "\"\"") : "";
        return String.format("%s,%s,%s,%s,%s,\"%s\"",
                transactionId, type.name(), amount, balanceAfter, timestamp.toString(), safeDescription);
    }

    /**
     * Reconstructs a Transaction object from a CSV line.
     * 
     * @param line the comma-separated string
     * @return a new Transaction object
     */
    public static Transaction fromCSV(String line) {
        // Split by comma, but ignore commas inside double quotes
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        
        String tId = parts[0];
        TransactionType tType = TransactionType.valueOf(parts[1]);
        double amt = Double.parseDouble(parts[2]);
        double bal = Double.parseDouble(parts[3]);
        LocalDateTime ts = LocalDateTime.parse(parts[4]);
        
        String desc = parts.length > 5 ? parts[5] : "";
        if (desc.startsWith("\"") && desc.endsWith("\"")) {
            desc = desc.substring(1, desc.length() - 1).replace("\"\"", "\"");
        }
        
        return new Transaction(tId, tType, amt, bal, ts, desc);
    }
}
