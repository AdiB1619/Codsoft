package com.atm.exception;

/**
 * Exception thrown when a user exceeds the maximum number of allowed 
 * incorrect PIN attempts.
 */
public class MaxAttemptsExceededException extends Exception {
    public MaxAttemptsExceededException(String message) {
        super(message);
    }
}
