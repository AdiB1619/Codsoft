package com.atm.model;

import java.io.Serializable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a user's bank account in the system.
 * Holds account details such as account number, PIN, balance, and transaction history.
 * Implements Serializable for future data persistence support.
 */
public class BankAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The maximum amount of money a user can withdraw in a single day. */
    public static final double DAILY_WITHDRAWAL_LIMIT = 50000.0;

    /** The maximum number of consecutive failed PIN attempts before locking the account. */
    public static final int MAX_FAILED_ATTEMPTS = 3;

    /** The minimum balance that must be maintained in the account. */
    public static final double MINIMUM_BALANCE = 500.0;

    private String accountNumber;
    private String accountHolderName;
    private String hashedPIN;
    private double balance;
    private double dailyWithdrawnAmount;
    private LocalDate lastWithdrawalDate;
    private boolean isLocked;
    private int failedAttempts;
    private ArrayList<Transaction> transactionHistory;

    /**
     * Constructs a new BankAccount.
     * The plainTextPIN is hashed immediately upon creation using SHA-256.
     *
     * @param accountNumber     the unique account number (e.g., "ACC-0001")
     * @param accountHolderName the full name of the account owner
     * @param plainTextPIN      the raw PIN entered by the user
     * @param initialBalance    the starting balance for the account
     */
    public BankAccount(String accountNumber, String accountHolderName, String plainTextPIN, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.hashedPIN = com.atm.util.InputValidator.hashPIN(plainTextPIN);
        this.balance = initialBalance;
        this.dailyWithdrawnAmount = 0.0;
        this.lastWithdrawalDate = LocalDate.now();
        this.isLocked = false;
        this.failedAttempts = 0;
        this.transactionHistory = new ArrayList<>();
    }

    /**
     * Verifies the entered PIN against the stored hashed PIN.
     * If the PIN is incorrect, it increments the failed attempts counter.
     * If the maximum failed attempts are reached, the account is locked.
     *
     * @param enteredPIN the plain text PIN entered by the user
     * @return true if the PIN is correct and the account is not locked, false otherwise
     */
    public boolean verifyPIN(String enteredPIN) {
        if (isLocked) {
            return false; // Cannot verify PIN on a locked account
        }
        
        String enteredHash = com.atm.util.InputValidator.hashPIN(enteredPIN);
        if (this.hashedPIN.equals(enteredHash)) {
            this.failedAttempts = 0; // Reset attempts on successful login
            return true;
        } else {
            this.failedAttempts++;
            if (this.failedAttempts >= MAX_FAILED_ATTEMPTS) {
                this.isLocked = true;
            }
            return false;
        }
    }

    /**
     * Resets the daily withdrawn amount if the current day is different
     * from the last withdrawal date. Updates the last withdrawal date to today.
     */
    public void resetDailyLimitIfNewDay() {
        LocalDate today = LocalDate.now();
        if (lastWithdrawalDate == null || lastWithdrawalDate.isBefore(today)) {
            this.dailyWithdrawnAmount = 0.0;
            this.lastWithdrawalDate = today;
        }
    }

    /**
     * Calculates the remaining withdrawal limit for the current day.
     * Calls resetDailyLimitIfNewDay() internally to ensure accuracy.
     *
     * @return the remaining amount the user can withdraw today
     */
    public double getRemainingDailyLimit() {
        resetDailyLimitIfNewDay();
        return DAILY_WITHDRAWAL_LIMIT - this.dailyWithdrawnAmount;
    }

    /**
     * Adds a new transaction to the account's transaction history.
     * Sorts the history to ensure transactions remain ordered (newest first).
     *
     * @param t the transaction to add
     */
    public void addTransaction(Transaction t) {
        this.transactionHistory.add(t);
        Collections.sort(this.transactionHistory);
    }

    // ==========================================
    // GETTERS
    // ==========================================

    /**
     * Gets the account number.
     * @return the account number
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Gets the account holder's full name.
     * @return the account holder name
     */
    public String getAccountHolderName() {
        return accountHolderName;
    }

    /**
     * Gets the hashed PIN.
     * @return the SHA-256 hashed PIN
     */
    public String getHashedPIN() {
        return hashedPIN;
    }

    /**
     * Gets the current account balance.
     * @return the balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Gets the total amount withdrawn today.
     * @return the daily withdrawn amount
     */
    public double getDailyWithdrawnAmount() {
        return dailyWithdrawnAmount;
    }

    /**
     * Gets the date of the last withdrawal.
     * @return the last withdrawal date
     */
    public LocalDate getLastWithdrawalDate() {
        return lastWithdrawalDate;
    }

    /**
     * Checks if the account is currently locked.
     * @return true if locked, false otherwise
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Gets the number of consecutive failed login attempts.
     * @return the failed attempts count
     */
    public int getFailedAttempts() {
        return failedAttempts;
    }

    /**
     * Gets the list of all transactions associated with this account.
     * @return the transaction history
     */
    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    // ==========================================
    // SETTERS (Controlled Mutators)
    // ==========================================

    /**
     * Sets the account balance.
     * @param balance the new balance
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * Sets the daily withdrawn amount.
     * @param dailyWithdrawnAmount the new daily withdrawn amount
     */
    public void setDailyWithdrawnAmount(double dailyWithdrawnAmount) {
        this.dailyWithdrawnAmount = dailyWithdrawnAmount;
    }

    /**
     * Sets the date of the last withdrawal.
     * @param lastWithdrawalDate the new withdrawal date
     */
    public void setLastWithdrawalDate(LocalDate lastWithdrawalDate) {
        this.lastWithdrawalDate = lastWithdrawalDate;
    }

    /**
     * Sets the lock status of the account.
     * @param locked true to lock the account, false to unlock
     */
    public void setLocked(boolean locked) {
        this.isLocked = locked;
    }

    /**
     * Sets the consecutive failed PIN attempts counter.
     * @param failedAttempts the number of failed attempts
     */
    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    /**
     * Sets a new hashed PIN for the account.
     * @param hashedPIN the new SHA-256 hashed PIN
     */
    public void setHashedPIN(String hashedPIN) {
        this.hashedPIN = hashedPIN;
    }
}

