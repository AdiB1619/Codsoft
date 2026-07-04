package com.atm.exception;

/**
 * Thrown when an account is locked due to too many failed PIN attempts.
 */
public class AccountLockedException extends ATMException {
    
    /**
     * Constructs a new AccountLockedException with the standard support message.
     */
    public AccountLockedException() {
        super("Account locked after too many failed PIN attempts. Contact bank support: 1800-XXX-XXXX");
    }

    @Override
    public String getErrorCode() {
        return "ATM-005";
    }
}
