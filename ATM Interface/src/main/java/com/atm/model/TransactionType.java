package com.atm.model;

/**
 * Enumeration representing the different types of events and transactions
 * that can occur within the ATM system and be recorded in history.
 */
public enum TransactionType {
    
    /** Represents a cash or check deposit into the account. */
    DEPOSIT("Deposit", "+"),

    /** Represents a cash withdrawal from the account. */
    WITHDRAWAL("Withdrawal", "-"),

    /** Represents a user checking their account balance. */
    BALANCE_INQUIRY("Balance Inquiry", "="),

    /** Represents a successful login attempt by the user. */
    LOGIN_SUCCESS("Login Success", "✔"),

    /** Represents a failed login attempt due to incorrect credentials. */
    LOGIN_FAILED("Login Failed", "✘"),

    /** Represents an event where the account is locked due to security rules. */
    ACCOUNT_LOCKED("Account Locked", "🔒");

    private final String displayName;
    private final String symbol;

    /**
     * Constructs a new TransactionType.
     * 
     * @param displayName the human-readable name of the transaction type
     * @param symbol a symbol visually representing the effect or nature of the transaction
     */
    TransactionType(String displayName, String symbol) {
        this.displayName = displayName;
        this.symbol = symbol;
    }

    /**
     * Gets the human-readable display name of the transaction type.
     * 
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the symbol representing the transaction.
     * 
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns the display name of the transaction type.
     * 
     * @return the display name
     */
    @Override
    public String toString() {
        return displayName;
    }
}
