package com.atm.exception;

/**
 * Base custom unchecked exception for the ATM banking system.
 */
public abstract class ATMException extends RuntimeException {
    
    public ATMException(String message) {
        super(message);
    }
    
    public ATMException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Retrieves the specific error code associated with the exception.
     * @return the error code string
     */
    public abstract String getErrorCode();
}
