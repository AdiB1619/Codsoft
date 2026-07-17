package com.yourcompany.currencyconverter.exception;

/**
 * Thrown when attempting to create a resource that already exists,
 * such as adding a currency to favorites that is already favorited.
 */
public class DuplicateResourceException extends RuntimeException {
    
    public DuplicateResourceException(String message) {
        super(message);
    }
}
