package com.atm.exception;

/**
 * Thrown when an account does not have enough balance to complete a transaction.
 */
public class InsufficientFundsException extends ATMException {
    
    private final double requestedAmount;
    private final double availableBalance;

    /**
     * Constructs a new InsufficientFundsException.
     * @param requestedAmount the amount requested for withdrawal
     * @param availableBalance the currently available balance in the account
     */
    public InsufficientFundsException(double requestedAmount, double availableBalance) {
        super(String.format("Insufficient funds. Requested: ₹%,.2f | Available: ₹%,.2f (minimum balance ₹500 must be maintained)", requestedAmount, availableBalance));
        this.requestedAmount = requestedAmount;
        this.availableBalance = availableBalance;
    }

    @Override
    public String getErrorCode() {
        return "ATM-001";
    }

    public double getRequestedAmount() {
        return requestedAmount;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }
}
