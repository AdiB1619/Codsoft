package com.atm.exception;

/**
 * Thrown when the user enters an incorrect PIN.
 */
public class InvalidPINException extends ATMException {
    
    private final int attemptsRemaining;

    /**
     * Constructs a new InvalidPINException tracking attempts.
     * @param attemptsRemaining the remaining chances before lockout
     */
    public InvalidPINException(int attemptsRemaining) {
        super(String.format("Invalid PIN. %d attempt(s) remaining.", attemptsRemaining));
        this.attemptsRemaining = attemptsRemaining;
    }
    
    /**
     * Backwards compatible constructor for simple format checks.
     * @param message custom exception message
     */
    public InvalidPINException(String message) {
        super(message);
        this.attemptsRemaining = 0;
    }

    @Override
    public String getErrorCode() {
        return "ATM-004";
    }

    public int getAttemptsRemaining() {
        return attemptsRemaining;
    }
}
