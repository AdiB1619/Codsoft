package com.atm.exception;

/**
 * Thrown when an amount provided for a transaction is invalid.
 */
public class InvalidAmountException extends ATMException {
    
    private final double invalidAmount;
    private final String reason;

    /**
     * Constructs a new InvalidAmountException.
     * @param invalidAmount the bad amount
     * @param reason the string reasoning for the failure
     */
    public InvalidAmountException(double invalidAmount, String reason) {
        super(String.format("Invalid amount ₹%,.2f: %s", invalidAmount, reason));
        this.invalidAmount = invalidAmount;
        this.reason = reason;
    }

    @Override
    public String getErrorCode() {
        return "ATM-002";
    }

    public double getInvalidAmount() {
        return invalidAmount;
    }

    public String getReason() {
        return reason;
    }
}
