package com.atm.exception;

/**
 * Thrown when a withdrawal amount would breach the user's daily limit.
 */
public class DailyLimitExceededException extends ATMException {
    
    private final double requestedAmount;
    private final double remainingLimit;

    /**
     * Constructs a new DailyLimitExceededException.
     * @param requestedAmount the requested withdrawal amount
     * @param remainingLimit the user's remaining daily limit
     */
    public DailyLimitExceededException(double requestedAmount, double remainingLimit) {
        super(String.format("Daily withdrawal limit exceeded. Requested: ₹%,.2f | Remaining today: ₹%,.2f", requestedAmount, remainingLimit));
        this.requestedAmount = requestedAmount;
        this.remainingLimit = remainingLimit;
    }

    @Override
    public String getErrorCode() {
        return "ATM-003";
    }

    public double getRequestedAmount() {
        return requestedAmount;
    }

    public double getRemainingLimit() {
        return remainingLimit;
    }
}
