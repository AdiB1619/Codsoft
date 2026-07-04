package com.atm.service;

import com.atm.exception.DailyLimitExceededException;
import com.atm.exception.InsufficientFundsException;
import com.atm.exception.InvalidAmountException;
import com.atm.model.BankAccount;
import com.atm.model.Transaction;
import com.atm.model.TransactionType;

import java.text.DecimalFormat;

/**
 * Service responsible for processing financial transactions.
 * Handles deposits, withdrawals, and balance inquiries, ensuring business rules 
 * (like sufficient funds or daily limits) are enforced.
 */
public class TransactionService {

    private BankAccount account;

    /**
     * Constructs a TransactionService for a specific bank account.
     * 
     * @param account the bank account to perform transactions on
     */
    public TransactionService(BankAccount account) {
        this.account = account;
    }

    /**
     * Deposits a specified amount into the bank account.
     * 
     * @param amount the amount to deposit
     * @return the generated Transaction record
     * @throws InvalidAmountException if the amount is <= 0 or exceeds the max deposit limit
     */
    public Transaction deposit(double amount) throws InvalidAmountException {
        amount = com.atm.util.InputValidator.validatePositiveAmount(amount);
        if (amount > 100000.0) {
            throw new InvalidAmountException(amount, "Maximum single deposit limit is ₹1,00,000.00.");
        }

        double newBalance = account.getBalance() + amount;
        account.setBalance(newBalance);

        Transaction transaction = new Transaction(
                TransactionType.DEPOSIT,
                amount,
                newBalance,
                "Cash deposit of " + formatAmount(amount)
        );
        account.addTransaction(transaction);
        
        return transaction;
    }

    /**
     * Withdraws a specified amount from the bank account.
     * 
     * @param amount the amount to withdraw
     * @return the generated Transaction record
     * @throws InvalidAmountException if amount <= 0 or not a multiple of 100
     * @throws InsufficientFundsException if the account has insufficient balance
     * @throws DailyLimitExceededException if the daily withdrawal limit is exceeded
     */
    public Transaction withdraw(double amount) throws InvalidAmountException, InsufficientFundsException, DailyLimitExceededException {
        account.resetDailyLimitIfNewDay();

        amount = com.atm.util.InputValidator.validatePositiveAmount(amount);
        amount = com.atm.util.InputValidator.validateWithdrawalAmount(
                amount, 
                account.getBalance(), 
                account.getDailyWithdrawnAmount(), 
                BankAccount.DAILY_WITHDRAWAL_LIMIT, 
                BankAccount.MINIMUM_BALANCE
        );

        double newBalance = account.getBalance() - amount;
        account.setBalance(newBalance);
        account.setDailyWithdrawnAmount(account.getDailyWithdrawnAmount() + amount);
        account.setLastWithdrawalDate(java.time.LocalDate.now());

        Transaction transaction = new Transaction(
                TransactionType.WITHDRAWAL,
                amount,
                newBalance,
                "Cash withdrawal of " + formatAmount(amount)
        );
        account.addTransaction(transaction);
        
        return transaction;
    }

    /**
     * Records a balance inquiry event in the transaction history.
     * 
     * @return the generated Transaction record
     */
    public Transaction recordBalanceInquiry() {
        Transaction transaction = new Transaction(
                TransactionType.BALANCE_INQUIRY,
                0.0,
                account.getBalance(),
                "Balance inquiry"
        );
        account.addTransaction(transaction);
        
        return transaction;
    }

    /**
     * Formats a monetary amount into a string with currency symbol and commas.
     * 
     * @param amount the amount to format
     * @return the formatted amount string
     */
    private String formatAmount(double amount) {
        return "₹" + new DecimalFormat("#,##0.00").format(amount);
    }
}
