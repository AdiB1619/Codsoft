package com.atm.exception;

/**
 * Exception thrown when an operation references an account number
 * that does not exist in the system.
 */
public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
