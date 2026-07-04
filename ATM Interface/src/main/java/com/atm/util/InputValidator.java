package com.atm.util;

import com.atm.exception.DailyLimitExceededException;
import com.atm.exception.InsufficientFundsException;
import com.atm.exception.InvalidAmountException;
import com.atm.exception.InvalidPINException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class containing common validation rules and security functions.
 */
public class InputValidator {

    private InputValidator() {} // Prevent instantiation

    /**
     * Validates that an amount is strictly positive and within the maximum single transaction limit.
     * 
     * @param amount the amount to validate
     * @return the validated amount
     * @throws InvalidAmountException if amount is zero, negative, or exceeds 1,000,000
     */
    public static double validatePositiveAmount(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(amount, "Amount must be greater than zero.");
        }
        if (amount > 1_000_000.0) {
            throw new InvalidAmountException(amount, "Amount exceeds maximum allowed per transaction.");
        }
        return amount;
    }

    /**
     * Chains all withdrawal-specific validations to ensure a transaction is legal.
     * 
     * @param amount         the withdrawal amount requested
     * @param currentBalance the account's current balance
     * @param dailyWithdrawn the amount the user has already withdrawn today
     * @param dailyLimit     the maximum amount allowed per day
     * @param minimumBalance the minimum balance required in the account
     * @return the validated amount
     * @throws InvalidAmountException if amount is not a multiple of 100
     * @throws InsufficientFundsException if withdrawal breaches the minimum balance limit
     * @throws DailyLimitExceededException if withdrawal breaches the daily limit
     */
    public static double validateWithdrawalAmount(double amount, double currentBalance, 
                                           double dailyWithdrawn, double dailyLimit,
                                           double minimumBalance) 
                                           throws InvalidAmountException, InsufficientFundsException, DailyLimitExceededException {
        if (amount % 100 != 0) {
            throw new InvalidAmountException(amount, "ATMs only dispense in multiples of ₹100.");
        }
        if (currentBalance - amount < minimumBalance) {
            throw new InsufficientFundsException(amount, currentBalance);
        }
        if (dailyWithdrawn + amount > dailyLimit) {
            double remainingLimit = Math.max(0, dailyLimit - dailyWithdrawn);
            throw new DailyLimitExceededException(amount, remainingLimit);
        }
        return amount;
    }

    /**
     * Validates that a PIN is exactly 4 digits.
     * 
     * @param pin the PIN string to validate
     * @return the validated PIN
     * @throws InvalidPinException if the PIN is not exactly 4 digits
     */
    public static String validatePIN(String pin) throws InvalidPINException {
        if (isNullOrEmpty(pin) || !pin.matches("\\d{4}")) {
            throw new InvalidPINException("PIN must be exactly 4 digits.");
        }
        return pin;
    }

    /**
     * Validates that a menu choice is within a specified range.
     * 
     * @param choice the user's input choice
     * @param min    the minimum acceptable integer (inclusive)
     * @param max    the maximum acceptable integer (inclusive)
     * @return the validated choice
     * @throws IllegalArgumentException if the choice is out of bounds
     */
    public static int validateMenuChoice(int choice, int min, int max) throws IllegalArgumentException {
        if (choice < min || choice > max) {
            throw new IllegalArgumentException("Choice must be between " + min + " and " + max + ".");
        }
        return choice;
    }

    /**
     * Checks if a string is null, empty, or consists only of whitespace.
     * 
     * @param str the string to check
     * @return true if null or blank, false otherwise
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Hashes a plain text PIN using the SHA-256 algorithm.
     * 
     * @param plainPIN the plain text PIN
     * @return the hexadecimal string representation of the hashed PIN
     */
    public static String hashPIN(String plainPIN) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPIN.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: SHA-256 algorithm not found", e);
        }
    }
}
